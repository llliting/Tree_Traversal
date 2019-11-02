import java.io.IOException;

public class text{

	public static void main(String[] args){
  		String[] input = new String[] {"a","3", "*", "5","r", "*", "+"};
 		Expression x = Expression.expressionFromPostfix(input);
 		try{x.drawExpression("expr.dot");
 		}catch(IOException ioe){
 			System.exit(0);
 		}
 		System.out.println(x.toPrefix());
 		System.out.println(x.toPostfix());
 		System.out.println(x.toInfix());



 		
}



}