// LongInt ADT for unbounded integers
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.Integer;

public class LongInt {

	public static final int MAX_SIZE = 500;
	public static final int UPPERBOUND = 10000; // exclusive
	public static final int UPPERBOUND_LEN = 4;
	public static final Pattern EXPRESSION_PATTERN = Pattern.compile("(?<sign>[[+][-]]?)(?<num>[0-9]+)");
	
	private String sign; // empty string for positive sign
	private int[] intArr;
	private int startIndex;
	
	// constructor
	public LongInt(String s) 
	{
		Matcher matcher = EXPRESSION_PATTERN.matcher(s);
		String num_str = "";
		int len = 0;
		
		if (matcher.find())
		{
			this.sign = matcher.group("sign");
			num_str = matcher.group("num");
			len = num_str.length(); // set len
		}
		
		// convert to int array (store 9 digits for each int)
		intArr = new int[MAX_SIZE];
		int num;
		for(int i = 0; i <= len/UPPERBOUND_LEN; i++)
		{
			try
			{
				num = Integer.valueOf(num_str.substring(len - (i+1)*UPPERBOUND_LEN, len - i*UPPERBOUND_LEN));
			}
			catch(IndexOutOfBoundsException e) // leftover digits at the front
			{
				if (len % UPPERBOUND_LEN != 0)
					num = Integer.valueOf(num_str.substring(0, len - i*UPPERBOUND_LEN));
				else
					num = 0;
			}
			
			intArr[intArr.length - (i+1)] = num;
		}
		startIndex = setStartIndex(intArr);
	}
	
	public LongInt(int[] numArr, String sign)
	{
		this.intArr = numArr;
		this.sign = sign;
		this.startIndex = setStartIndex(this.intArr);
	}

	// returns 'this' + 'opnd'; Both inputs remain intact.
	public LongInt add(LongInt opnd) 
	{
		String resultSign;
		int[] resultArr;
		
		if (this.sign.equals(opnd.getSign()))
		{
			resultSign = this.sign;
			resultArr = addArrays(opnd);
		}
		else
		{
			if (!isSmallerAbsolute(opnd)) // this is bigger than opnd
				resultSign = this.sign;
			else
				resultSign = opnd.getSign();
			
			resultArr = subtractArrays(opnd);
		}
		
		return new LongInt(resultArr, resultSign);
	}

	// returns 'this' - 'opnd'; Both inputs remain intact.
	public LongInt subtract(LongInt opnd) 
	{
		String resultSign;
		int[] resultArr;
		
		if (this.sign.equals(opnd.getSign()))
		{
			if (!isSmallerAbsolute(opnd)) // this is bigger or equal than opnd absolutely
				resultSign = this.sign;
			else
				resultSign = getOppositeSign(this.sign);
			
			resultArr = subtractArrays(opnd);
		}
		else
		{
			resultSign = this.sign;
			resultArr = addArrays(opnd);
		}
		
		return new LongInt(resultArr, resultSign);
	}

	// returns 'this' * 'opnd'; Both inputs remain intact.
	public LongInt multiply(LongInt opnd) 
	{
		int[] resultArr = new int[MAX_SIZE];
		LongInt resultLong = new LongInt(resultArr, "");
		String resultSign;
		
		// when either is 0
		if (this.startIndex == MAX_SIZE - 1 || opnd.getStartIndex() == MAX_SIZE - 1)
		{
			if (this.intArr[MAX_SIZE-1] == 0 || opnd.getArray()[MAX_SIZE-1] == 0)
				return resultLong;
		}
		
		if (this.sign.equals(opnd.getSign()))
			resultSign = "";
		else
			resultSign = "-";
		
		for (int i = 0; i < MAX_SIZE/2; i++)
		{
			for (int k = 0; k < MAX_SIZE/2; k++)
			{
				int[] tmpArr = new int[MAX_SIZE];
				int val = this.intArr[MAX_SIZE - (i+1)] * opnd.getArray()[MAX_SIZE - (k+1)] 
						  + tmpArr[MAX_SIZE - (i+k+1)];
				tmpArr[MAX_SIZE - (i+k+1)] = val % UPPERBOUND;
				if (MAX_SIZE - (i+k+2) >= 0)
					tmpArr[MAX_SIZE - (i+k+2)] += val / UPPERBOUND;
				
				LongInt tmpLong = new LongInt(tmpArr, "");
				resultLong = resultLong.add(tmpLong);
			}
		}
		return new LongInt(resultLong.getArray(), resultSign);
	}

	// print the value of 'this' element to the standard output.
	public void print() 
	{
		String outputStr = this.sign;
		outputStr += Integer.toString(this.intArr[this.startIndex]);
		for(int i = this.startIndex+1; i < MAX_SIZE; i++)
		{
			outputStr += leadingZeros(this.intArr[i]) + Integer.toString(this.intArr[i]);
		}	
		System.out.print(outputStr);
	}

	public String getSign()
	{
		return this.sign;
	}
	
	public int[] getArray()
	{
		return this.intArr;
	}
	
	public int getStartIndex()
	{
		return this.startIndex;
	}
	
	// if this is absolutely smaller that opnd return true, otherwise return false
	private boolean isSmallerAbsolute(LongInt opnd)
	{
		boolean smallerFlag = false;
		for(int i = 0; i < MAX_SIZE; i++)
		{
			if (this.intArr[i] < opnd.getArray()[i])
			{
				smallerFlag = true;
				break;
			}
			else if (this.intArr[i] > opnd.getArray()[i])
				break;
		}
		return smallerFlag;
	}
	
	// internal method for adding two arrays
	private int[] addArrays(LongInt opnd)
	{
		int[] resultArr = new int[MAX_SIZE];
		
		for(int i = MAX_SIZE-1; i >= 0; i--)
		{
			int added = resultArr[i] + this.intArr[i] + opnd.getArray()[i];
			resultArr[i] = added % UPPERBOUND;
			if (i > 0)
				resultArr[i-1] += added / UPPERBOUND;
		}
		return resultArr;
	}
	
	// internal method for subtracting two arrays (big - small)
	private int[] subtractArrays(LongInt opnd)
	{
		int[] resultArr = new int[MAX_SIZE];
		int[] bigArr;
		int[] smallArr;
		
		// determine big and small array
		if (!isSmallerAbsolute(opnd)) // this is bigger or equal than opnd
		{
			bigArr = this.intArr;
			smallArr = opnd.getArray();
		}
		else
		{
			bigArr = opnd.getArray();
			smallArr = this.intArr;
		}
		
		for(int i = MAX_SIZE-1; i >= 0; i--)
		{
			int subtracted = bigArr[i] - smallArr[i];
			if (subtracted < 0)
			{
				resultArr[i] += UPPERBOUND + subtracted;
				if (i > 0)
					resultArr[i-1] -= 1;
			}
			else
				resultArr[i] += subtracted;
		}
		convertMinusOne(resultArr);
		return resultArr;
	}
	
	private String getOppositeSign(String sign)
	{
		if (sign.equals(""))
			return "-";
		else
			return "";
	}
	
	// return leading zeros need for cipher match
	private String leadingZeros(int num)
	{
		int cnt = 1;
		while (num / 10 != 0)
		{
			num = num / 10;
			cnt++;
		}
		
		String zeros = "";
		for(int i = 0; i < UPPERBOUND_LEN - cnt; i++)
			zeros += "0";
		
		return zeros;
	}
	
	private void convertMinusOne(int[] arr)
	{
		for(int i = MAX_SIZE - 1; i > 0; i--)
		{
			if (arr[i] < 0)
			{
				arr[i] = UPPERBOUND - 1;
				arr[i-1] -= 1;
			}
		}
	}
	
	private int setStartIndex(int[] arr)
	{
		for(int i = 0; i <MAX_SIZE; i++)
		{
 			if (arr[i] == 0)
 				continue;
 			else
 				return i;
		}
		return MAX_SIZE-1;
	}
}
