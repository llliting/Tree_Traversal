import java.util.Set;
import java.util.HashMap;
import java.util.Scanner;
import java.io.IOException;

public class Calculate
{
   public static void main(String[] args) throws IOException
   {
      Scanner userIn = new Scanner(System.in);
      boolean anotherExpression = true;
      while(anotherExpression)
      {
	 String mode = "";
	 while(!mode.equals("p") && !mode.equals("i"))
	 {
	    System.out.print("Type p for postfix or i for infix: ");
	    mode = userIn.nextLine();
	 }

	 System.out.print("Please type your expression (with a single space between each token): ");
	 String strExpr = userIn.nextLine();

	 Expression expr = null;
	 if(mode.equals("p"))
	 {
	    expr = Expression.expressionFromPostfix(strExpr.split(" "));
	 }
	 else
	 {
	    expr = Expression.expressionFromInfix(strExpr.split(" "));      
	 }

 		try{expr.drawExpression("expr.dot");
 		}catch(IOException ioe){
 			System.exit(0);
 		}
	 
	 System.out.println("Postfix: " + expr.toPostfix());
	 System.out.println("Prefix: " + expr.toPrefix());
	 System.out.println("Infix: " + expr.toInfix());
	 
	 Expression simple = expr.simplify();
	 System.out.println("\nSimplified: " + simple);
	 
	 Set<String> variables = expr.getVariables();
	 HashMap<String, Integer> assignment = new HashMap<String, Integer>();
	 boolean anotherAssignment = true;
	 while(variables.size() > 0 && anotherAssignment)
	 {
	    System.out.println("\nPlease assign integer values to the variables:");
	    for(String v : variables)
	    {
	       System.out.print(v + " = ");
	       int i = userIn.nextInt();
	       assignment.put(v, i);
	    }	  
	    assignment.put("not_yet_implemented", 0);
	    
	    System.out.println("\nThe expression evaluates to: " + expr.evaluate(assignment));
	    System.out.println("The simplified expression evaluates to: " + simple.evaluate(assignment));
	    System.out.print("Would you like to reassign the variables (y/n)? ");
	    String answer = userIn.next();
	    if(!answer.equalsIgnoreCase("y"))
	    {
	       anotherAssignment = false;
	    }
	 }

	 System.out.print("Would you like to type another expression (y/n)? ");
	 String answer = userIn.next();
	 if(!answer.equalsIgnoreCase("y"))
	 {
	    anotherExpression = false;
	 }	 
      }
   }
}
