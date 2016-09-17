// LongInt ADT for unbounded integers
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.Integer;

public class LongInt {

	public static final int MAX_SIZE = 50;
	public static final int UPPERBOUND = 100000000; // exclusive
	public static final int UPPERBOUND_LEN = 9;
	public static final Pattern EXPRESSION_PATTERN = Pattern.compile("(?<sign>[[+][-]]?)(?<num>[0-9]+)");
	
	private char sign;
	private int[] intArr;
	private int len;
	
	// constructor
	public LongInt(String s) 
	{
		Matcher matcher = EXPRESSION_PATTERN.matcher(s);
		String num_str = "";
		if (matcher.find())
		{
			if (matcher.group("sign").equals("-"))
				this.sign = '-';
			else
				this.sign = '+';
			
			num_str = matcher.group("num");
			this.len = num_str.length(); // set len
		}
		
		// convert to int array
		intArr = new int[MAX_SIZE];
		int num;
		for(int i = 0; i <= this.len/UPPERBOUND_LEN; i++)
		{
			try
			{
				num = Integer.valueOf(num_str.substring(this.len - (i+1)*UPPERBOUND_LEN, 
                                                            this.len - i*UPPERBOUND_LEN));
			}
			catch(IndexOutOfBoundsException e) // for leftover digits at the front
			{
				num = Integer.valueOf(num_str.substring(0, this.len - i*UPPERBOUND_LEN));
			}
			
			intArr[intArr.length - (i+1)] = num;
		}
	}

	// returns 'this' + 'opnd'; Both inputs remain intact.
	public LongInt add(LongInt opnd) 
	{}

	// returns 'this' - 'opnd'; Both inputs remain intact.
	public LongInt subtract(LongInt opnd) 
	{}

	// returns 'this' * 'opnd'; Both inputs remain intact.
	public LongInt multiply(LongInt opnd) 
	{}

	// print the value of 'this' element to the standard output.
	public void print() {}

}
