// LongInt ADT for unbounded integers
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.Integer;

public class LongInt {

	public static final int MAX_SIZE = 50;
	public static final int UPPERBOUND = 100000000; // exclusive
	public static final int UPPERBOUND_LEN = 9;
	public static final Pattern EXPRESSION_PATTERN = Pattern.compile("(?<sign>[[+][-]]?)(?<num>[0-9]+)");
	
	private String sign; // empty string for positive sign
	private int[] intArr;
	
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
	}
	
	public LongInt(int[] numArr, String sign)
	{
		this.intArr = numArr;
		this.sign = sign;
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
			if (!isSmallerAbsolute(opnd))
				resultSign = this.sign;
			else
				resultSign = opnd.getSign();
			
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
		
		if (this.sign.equals(opnd.getSign()))
			resultSign = "";
		else
			resultSign = "-";
		
		for (int i = 0; i < MAX_SIZE; i++)
		{
			for (int k = 0; k < MAX_SIZE; k++)
			{
				int[] tmpArr = new int[MAX_SIZE];
				int multiplied = this.intArr[MAX_SIZE - (i+1)] * opnd.getArray()[MAX_SIZE - (k+1)];
				tmpArr[MAX_SIZE - (i+k+1)] += multiplied % UPPERBOUND;
				if (MAX_SIZE - (i+k+2) != 0)
					tmpArr[MAX_SIZE - (i+k+2)] += multiplied / UPPERBOUND;
				
				LongInt tmpLong = new LongInt(tmpArr, "");
				resultLong = resultLong.add(tmpLong);
			}
		}
		return new LongInt(resultLong.getArray(), resultSign);
	}

	// print the value of 'this' element to the standard output.
	public void print() {}

	public String getSign()
	{
		return this.sign;
	}
	
	public int[] getArray()
	{
		return this.intArr;
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
			int added = this.intArr[i] + opnd.getArray()[i];
			resultArr[i] += added % UPPERBOUND;
			
			if (i != 0)
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
		
		if (isSmallerAbsolute(opnd))
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
				if (i != 0)
					resultArr[i-1] -= 1;
			}
			else
			{
				resultArr[i] += subtracted;
			}
		}
		
		return resultArr;
	}
}
