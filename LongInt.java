// LongInt ADT for unbounded integers
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.Integer;

public class LongInt {

	public static final int UPPERBOUND = 10000; // exclusive
	public static final int UPPERBOUND_LEN = 4;
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
		intArr = new int[len/UPPERBOUND_LEN + 1];
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
		this.intArr = removeFrontZeros(intArr);
	}
	
	public LongInt(int[] numArr, String sign)
	{
		this.intArr = removeFrontZeros(numArr);
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
		// when either is 0, answer is 0
		if (this.intArr.length == 1 && this.intArr[0] == 0)
			return new LongInt(new int[1], "");
		else if (opnd.getArray().length == 1 && this.intArr[0] == 0)
			return new LongInt(new int[1], "");
		
		String resultSign;
		if (this.sign.equals(opnd.getSign()))
			resultSign = "";
		else
			resultSign = "-";
		
		int thisLen = this.intArr.length;
		int opndLen = opnd.getArray().length;
		int tmpLen = thisLen + opndLen + 1;
		LongInt resultLong = new LongInt(new int[tmpLen], "");
		
		for (int i = 0; i < thisLen; i++)
		{
			for (int k = 0; k < opndLen; k++)
			{
				int[] tmpArr = new int[tmpLen];
				int val = this.intArr[thisLen - (i+1)] * opnd.getArray()[opndLen - (k+1)] 
						  + tmpArr[tmpLen - (i+k+1)];
				tmpArr[tmpLen - (i+k+1)] = val % UPPERBOUND;
				if (tmpLen - (i+k+2) >= 0)
					tmpArr[tmpLen - (i+k+2)] += val / UPPERBOUND;
				
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
		outputStr += Integer.toString(this.intArr[0]);
		for(int i = 1; i < this.intArr.length; i++)
			outputStr += leadingZeros(this.intArr[i]) + Integer.toString(this.intArr[i]);
		
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
	
	// if this is smaller that opnd return true in absolute number, otherwise return false
	private boolean isSmallerAbsolute(LongInt opnd)
	{
		if (this.intArr.length > opnd.getArray().length)
			return false;
		else if (this.intArr.length < opnd.getArray().length)
			return true;
		
		// compare each digit
		for (int i = 0; i < this.intArr.length; i++)
		{
			if (this.intArr[i] > opnd.getArray()[i])
				return false;
			else if (this.intArr[i] < opnd.getArray()[i])
				return true;
		}
		return false;
	}
	
	// internal method for adding two arrays
	private int[] addArrays(LongInt opnd)
	{
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
		
		int[] resultArr = new int[bigArr.length + 1];
		
		for(int i = 0; i < smallArr.length; i++)
		{
			int added = resultArr[resultArr.length - (i+1)] + bigArr[bigArr.length - (i+1)] 
					    + smallArr[smallArr.length - (i+1)];
			resultArr[resultArr.length - (i+1)] = added % UPPERBOUND;
			resultArr[resultArr.length - (i+2)] += added / UPPERBOUND;
		}
		
		for (int k = smallArr.length; k < bigArr.length; k++)
		{
			int val = resultArr[resultArr.length - (k+1)] + bigArr[bigArr.length - (k+1)];
			resultArr[resultArr.length - (k+1)] = val % UPPERBOUND;
			resultArr[resultArr.length - (k+2)] = val / UPPERBOUND;
		}
		return resultArr;
	}
	
	// internal method for subtracting two arrays (big - small)
	private int[] subtractArrays(LongInt opnd)
	{
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
		
		int[] resultArr = new int[bigArr.length];
		
		for(int i = 0; i < smallArr.length; i++)
		{
			int subtracted = bigArr[bigArr.length - (i+1)] - smallArr[smallArr.length - (i+1)];
			if (subtracted < 0)
			{
				resultArr[resultArr.length - (i+1)] += UPPERBOUND + subtracted;
				resultArr[resultArr.length - (i+2)] -= 1;
			}
			else
				resultArr[resultArr.length - (i+1)] += subtracted;
		}
		
		for (int k = smallArr.length; k < bigArr.length; k++)
			resultArr[resultArr.length - (k+1)] += bigArr[bigArr.length - (k+1)];
		
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
	
	// convert -1 in the array to appropriate number
	private void convertMinusOne(int[] arr)
	{
		for(int i = arr.length - 1; i > 0; i--)
		{
			if (arr[i] < 0)
			{
				arr[i] = UPPERBOUND - 1;
				arr[i-1] -= 1;
			}
		}
	}
	
	// return array that has no front zeros
	private int[] removeFrontZeros(int[] arr)
	{
		int start = 0;
		for(int i = 0; i <arr.length - 1; i++)
		{
 			if (arr[i] == 0) start++;
 			else break;
		}

		// copy arr to retArr
		int[] retArr = new int[arr.length - start];
		for (int k = 0; k < retArr.length; k++)
			retArr[k] = arr[k + start];
		
		return retArr;
	}
}
