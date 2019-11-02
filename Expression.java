import java.util.*;
import java.io.*;
import java.lang.*;

/**
 * A class representing an abstract arithmetic expression
 */
public abstract class Expression
{
   /**
    * Creates a tree from an expression in postfix notation
    * @param postfix an array of Strings representing a postfix arithmetic expression
    * @return a new Expression that represents postfix
    */
   public static Expression expressionFromPostfix(String[] postfix)
   {
      String[] operators = {"+", "-", "*", "/"};
      Stack<Expression> children = new Stack<Expression>();
      Expression res = null;
      for(int i = 0; i < postfix.length; i ++ ){
        String curr = postfix[i];
        if(curr.equals("+")||curr.equals("-")||curr.equals("*")||curr.equals("/")){
          Expression right = children.pop();
          Expression left = children.pop();
          res = buildOperator(curr,left,right);
        }
        else{
          res = buildOperand(curr);
        }children.push(res); 
      }return res;
   }

   /**
    * Creates a helper method to determine if a string can convert to an integer
    * @param x a string that needs to be checked its type
    * @return a boolean telling if this string is a number 
    */
   private static boolean isInteger(String x){
        try{
          Integer.parseInt(x);
          return true;
        }catch(Exception e){
          return false;}
    }    


    /**
    * Creates a helper method to build an operator expression
    * @param curr a String represents the operator
    * @param left a Expression that is the left child of the new Expression
    * @param right a Expression that is the right child of the new Expression
    * @return a OperatorExpression
    */
   private static OperatorExpression buildOperator(String curr, Expression left, Expression right){
        OperatorExpression res = null;
        if(curr.equals("+")){
           res = new SumExpression(left,right);}
        else if(curr.equals("-")){
           res = new DifferenceExpression(left,right);}
        else if(curr.equals("*")){
           res = new ProductExpression(left,right);}
        else if(curr.equals("/")){
           res = new QuotientExpression(left,right);}
        return res;
     }


   /**
    * Creates a Operand Expression from an integer or a variable string
    * @param curr a String version of the current operator 
    * @return a new Operand that represents the Expression of current operand
    */    
   private static Operand buildOperand(String curr){
    if(isInteger(curr)){
      return new IntegerOperand(Integer.parseInt(curr));}
    return new VariableOperand(curr);
    }

   /**
    * Creates a tree from an expression in infix notation
    * @param infix an array of Strings representing a infix arithmetic expression
    * @return a new Expression that represents infix
    */
   public static Expression expressionFromInfix(String[] infix)
   {
      HashMap<String, Integer> order= new HashMap<String, Integer>();//build a hashmap of operator orders 
      order.put("(",1);
      order.put(")",1);
      order.put("+",2);
      order.put("-",2);
      order.put("*",3);
      order.put("/",3);
      //build two stacks that one to hold operators and another to hold operands and new Expression operands(build from operators)
      Stack<String> operator = new Stack<String>();
      Stack<Expression> operands = new Stack<Expression>();
      for(int i = 0; i < infix.length; i++){
        String curr = infix[i];
        if(curr.equals("+")||curr.equals("-")||curr.equals("*")||curr.equals("/")||curr.equals("(")||curr.equals(")")){//if the current one is a operator
          if(operator.empty() || curr.equals("(") || (operator.peek().equals("("))){//operator stack is empty
            operator.push(curr);
          }
          else if(order.get(curr) > order.get(operator.peek())){
            //current operator has a higher order than the previous one
            Expression left = operands.pop();
            String next = infix[i + 1];
            while(next.equals("(")){//in case two or more parentheses are next to each other
              i++;
              next = infix[i + 1];
              operator.push("(");
            }
            operands.push(buildOperator(curr,left,buildOperand(next)));
            i++;
          }
          else if(curr.equals(")")){//")"; need to pop all stuffs between parentheses 
            if(!operator.peek().equals("(")){//in case there's no operator between parentheses
              while(!operator.peek().equals("(")){
                Expression right = operands.pop();
                operands.push(buildOperator(operator.pop(),operands.pop(),right));
            }}operator.pop();//pop("(")
          }
          else{//the next operator has a lower or equal order than the previous one           
            Expression right = operands.pop(); 
            operands.push(buildOperator(operator.pop(),operands.pop(),right));
            operator.push(curr);  
          }
        }

       else{//push the item to the operand if it is not an operator
        operands.push(buildOperand(curr));
      }
    }
   while(!operator.empty()){//build all the rest operators to expressions 
      Expression right = operands.pop(); 
      operands.push(buildOperator(operator.pop(),operands.pop(),right));
   }
    return operands.pop();//pop the last item in the operand stack
  }




   /**
    * @return a String that represents this expression in prefix notation.
    */
   public abstract String toPrefix();

   /**
    * @return a String that represents this expression in infix notation.
    */  
   public abstract String toInfix();

   /**
    * @return a String that represents this expression in postfix notation.
    */  
   public abstract String toPostfix();

   /**
    * @return a String that represents the expression in infix notation
    */
   @Override
   public String toString()
   {
      return toInfix();
   }
   
   /**
    * @return a new Expression mathematically equivalent to this one, but simplified.
    */  
   public abstract Expression simplify();

   /**
    * Evaluates the expression given assignments of values to variables.
    * @param assignments a HashMap from Strings (variable names) to Integers (values).
    * @return the result of evaluating the expression with the given variable assignments
    */
   public abstract int evaluate(HashMap<String, Integer> assignments);

   /**
    * @return a Set of the variables contained in this expression
    */
   public abstract Set<String> getVariables();

   @Override
   public abstract boolean equals(Object obj);

   /**
    * Prints the expression as a tree in DOT format for visualization
    * @param filename the name of the output file
    */
   public void drawExpression(String filename) throws IOException
   {
      BufferedWriter bw = null;
      FileWriter fw = new FileWriter(filename);
      bw = new BufferedWriter(fw);
      
      bw.write("graph Expression {\n");
      
      drawExprHelper(bw);
      
      bw.write("}\n");
      
      bw.close();
      fw.close();     
   }

   /**
    * Recursively prints the vertices and edges of the expression tree for visualization
    * @param bw the BufferedWriter to write to
    */
   protected abstract void drawExprHelper(BufferedWriter bw) throws IOException;


}

/**
 * A class representing an abstract operand
 */
abstract class Operand extends Expression
{
  protected String item;//add a common instance variable for both integer&variable
  /**
  * @return a String that represents this expression in prefix notation.
  */   
  public String toPrefix()
   {
      return " " + item;
   }

   /**
    * @return a String that represents this expression in postfix notation.
    */  
   public String toPostfix()
   {
      return " " + item;
   }   

   /**
    * @return a String that represents the expression in infix notation
    */
   public String toInfix()
   {
      return  item;     
   }

   /**
    * @return a new Expression mathematically equivalent to this one, but simplified.
    */  
   public Expression simplify()
   {
      
      return this;
   }   

    /**
    * @param obj and Object to compare to
    * @return true if obj is an IntegerOperand with the same associated value
    */
   @Override
   public boolean equals(Object obj)
   {

      if (obj == null)
        return false;
      else if (this == obj) // Refer to same object in memory or not
        return true;
      else if (!(obj instanceof Operand)) 
        return false;

      Operand exp2 = (Operand) obj;//build a new object to check the instance variables 
      if (item.equals(exp2.item))
        return true;
      return false;
   }   

    /**
    * Recursively prints the vertices and edges of the expression tree for visualization
    * @param bw the BufferedWriter to write to
    */
   protected void drawExprHelper(BufferedWriter bw) throws IOException
   {
      bw.write("\tnode"+hashCode()+"[label="+item+"];\n");
   }
}

/**
 * A class representing an expression containing only a single integer value
 */
class IntegerOperand extends Operand
{
   protected int operand;

   /**
    * Create the expression
    * @param operand the integer value this expression represents
    */
   public IntegerOperand(int operand)
   {
      this.operand = operand;
      this.item = Integer.toString(operand);
   }

   /**
    * @return a Set of the variables contained in this expression
    */
   public Set<String> getVariables()
   {
      return new TreeSet<String>();
   }

    /**
    * Evaluates the expression given assignments of values to variables.
    * @param assignments a HashMap from Strings (variable names) to Integers (values).
    * @return the result of evaluating the expression with the given variable assignments
    */
   public int evaluate(HashMap<String, Integer> assignments)
   {
      return operand;
   }

}

/**
 * A class representing an expression containing only a single variable
 */
class VariableOperand extends Operand
{
   /**
    * Create the expression
    * @param variable the variable name contained with this expression
    */
   public VariableOperand(String variable)
   {
      this.item = variable;
   }

   /**
    * Evaluates the expression given assignments of values to variables.
    * @param assignments a HashMap from Strings (variable names) to Integers (values).
    * @return the result of evaluating the expression with the given variable assignments
    */
   public int evaluate(HashMap<String, Integer> assignments)
   {
      return assignments.get(item);
   }


   /**
    * @return a Set of the variables contained in this expression
    */
   public Set<String> getVariables()
   {
      Set<String> vars = new TreeSet<String> ();
      vars.add(item);
      return vars;
   }
}

/**
 * A class representing an expression involving an operator
 */
abstract class OperatorExpression extends Expression
{
   protected Expression left;
   protected Expression right;

   /**
    * Create the expression
    * @param left the expression representing the left operand
    * @param right the expression representing the right operand
    */
   public OperatorExpression(Expression left, Expression right)
   {
      this.left = left;
      this.right = right;
   }

   /**
    * @return a string representing the operator
    */
   protected abstract String getOperator();     
   

   /**
    * @return a String that represents this expression in prefix notation.
    */   
   public String toPrefix()
   {
      return  " " + getOperator() + left.toPrefix() + right.toPrefix();
   }

   /**
    * @return a String that represents this expression in postfix notation.
    */  
   public String toPostfix()
   {
      return left.toPostfix() + right.toPostfix() + " " +getOperator();
   }   

   /**
    * @return a String that represents the expression in infix notation
    */
   public String toInfix()
   {
      return "(" + left.toInfix()  + getOperator() + right.toInfix() + ")";        
   }



    /**
    * @return a Set of the variables contained in this expression
    */
   public Set<String> getVariables()
   {  
      Set<String> res = left.getVariables();
      res.addAll(right.getVariables());
      return res;
   }

   /**
    * @param obj and Object to compare to
    * @return true if obj is an IntegerOperand with the same associated value
    */
   @Override
   public boolean equals(Object obj)
   {
      if (obj == null)
        return false;
      else if (this == obj) // Refer to same object in memory or not
        return true;
      else if (!(obj instanceof OperatorExpression)) 
        return false;

      OperatorExpression exp2 = (OperatorExpression) obj;
      if(!exp2.getOperator().equals(getOperator())){
        return false;
      }
      if(exp2.getOperator().equals("*")|| exp2.getOperator().equals("+")){
        if ((this.left.equals(exp2.left) && this.right.equals(exp2.right))||(this.left.equals(exp2.right) && this.right.equals(exp2.left)))
          return true;
      }
      else{
        if (this.left.equals(exp2.left) && this.right.equals(exp2.right))
          return true;
      }
      return false;
   }      

   /**
    * Recursively prints the vertices and edges of the expression tree for visualization
    * @param bw the BufferedWriter to write to
    */
   protected void drawExprHelper(BufferedWriter bw) throws IOException
   {
      String rootID = "\tnode"+hashCode();
      bw.write(rootID+"[label=\""+getOperator()+"\"];\n");

      bw.write(rootID + " -- node" + left.hashCode() + ";\n");
      bw.write(rootID + " -- node" + right.hashCode() + ";\n");
      left.drawExprHelper(bw);
      right.drawExprHelper(bw);
   }   
}

/**
 * A class representing an expression involving an sum
 */
class SumExpression extends OperatorExpression
{
   /**
    * Create the expression
    * @param left the expression representing the left operand
    * @param right the expression representing the right operand
    */
   public SumExpression(Expression left, Expression right)
   {
      super(left, right);
   }

   /**
    * @return a string representing the operand
    */
   protected String getOperator()
   {
      return "+";
   }

    /**
    * Evaluates the expression given assignments of values to variables.
    * @param assignments a HashMap from Strings (variable names) to Integers (values).
    * @return the result of evaluating the expression with the given variable assignments
    */
   public int evaluate(HashMap<String, Integer> assignments)
   {
      return left.evaluate(assignments) + right.evaluate(assignments);
   }

   /**
    * @return a new Expression mathematically equivalent to this one, but simplified.
    */  
   public Expression simplify()
   {  
      Expression leftSimp = left.simplify();
      Expression rightSimp = right.simplify();

      if(leftSimp instanceof IntegerOperand && rightSimp instanceof IntegerOperand) //evaluate directly if there is no variables after simplify each side
        return new IntegerOperand(leftSimp.evaluate(null) + rightSimp.evaluate(null));

      else if(leftSimp.equals(new IntegerOperand(0)))
        return rightSimp;

      else if(rightSimp.equals(new IntegerOperand(0)))
        return leftSimp;

      else 
        return new SumExpression(leftSimp,rightSimp); 
    }
}

/**
 * A class representing an expression involving an differnce
 */
class DifferenceExpression extends OperatorExpression
{
   /**
    * Create the expression
    * @param left the expression representing the left operand
    * @param right the expression representing the right operand
    */
   public DifferenceExpression(Expression left, Expression right)
   {
      super(left, right);
   }

   /**
    * @return a string representing the operand
    */
   protected String getOperator()
   {
      return "-";
   }

    /**
    * Evaluates the expression given assignments of values to variables.
    * @param assignments a HashMap from Strings (variable names) to Integers (values).
    * @return the result of evaluating the expression with the given variable assignments
    */
   public int evaluate(HashMap<String, Integer> assignments)
   {
      return left.evaluate(assignments) - right.evaluate(assignments);
   }

   
   /**
    * @return a new Expression mathematically equivalent to this one, but simplified.
    */  
   public Expression simplify() 
   {  

      Expression leftSimp = left.simplify();
      Expression rightSimp = right.simplify();

      if(leftSimp.equals(rightSimp))
        return new IntegerOperand(0);

      else if(leftSimp instanceof IntegerOperand && rightSimp instanceof IntegerOperand) 
        return new IntegerOperand(leftSimp.evaluate(null) - rightSimp.evaluate(null));

      else if(rightSimp.equals(new IntegerOperand(0)))
        return leftSimp;

      else 
        return new DifferenceExpression(leftSimp,rightSimp); 
   }    
}

/**
 * A class representing an expression involving a product
 */
class ProductExpression extends OperatorExpression
{
   /**
    * Create the expression
    * @param left the expression representing the left operand
    * @param right the expression representing the right operand
    */
   public ProductExpression(Expression left, Expression right)
   {
      super(left, right);
   }

   /**
    * @return a string representing the operand
    */
   protected String getOperator()
   {
      return "*";
   }

    /**
    * Evaluates the expression given assignments of values to variables.
    * @param assignments a HashMap from Strings (variable names) to Integers (values).
    * @return the result of evaluating the expression with the given variable assignments
    */
   public int evaluate(HashMap<String, Integer> assignments)
   {
      return left.evaluate(assignments) * right.evaluate(assignments);
   }
   /**
    * @return a new Expression mathematically equivalent to this one, but simplified.
    */  
   public Expression simplify()
   {  
      Expression leftSimp = left.simplify();
      Expression rightSimp = right.simplify();


      if(leftSimp.equals(new IntegerOperand(0)) || rightSimp.equals(new IntegerOperand(0)))
        return new IntegerOperand(0);

      else if(leftSimp instanceof IntegerOperand && rightSimp instanceof IntegerOperand) 
        return new IntegerOperand(leftSimp.evaluate(null) * rightSimp.evaluate(null));

      else if(leftSimp.equals(new IntegerOperand(1)))
        return rightSimp;

      else if(rightSimp.equals(new IntegerOperand(1)))
        return leftSimp;

      else 
        return new ProductExpression(leftSimp,rightSimp); 
   }   
}

/**
 * A class representing an expression involving a division
 */
class QuotientExpression extends OperatorExpression
{
   /**
    * Create the expression
    * @param left the expression representing the left operand
    * @param right the expression representing the right operand
    */
   public QuotientExpression(Expression left, Expression right)
   {
      super(left, right);
   }

   /**
    * @return a string representing the operand
    */
   protected String getOperator()
   {
      return "/";
   }

    /**
    * Evaluates the expression given assignments of values to variables.
    * @param assignments a HashMap from Strings (variable names) to Integers (values).
    * @return the result of evaluating the expression with the given variable assignments
    */
   public int evaluate(HashMap<String, Integer> assignments)
   {
      return left.evaluate(assignments) / right.evaluate(assignments);
   }

   /**
    * @return a new Expression mathematically equivalent to this one, but simplified.
    */  
   public Expression simplify()
   {  

      Expression leftSimp = left.simplify();
      Expression rightSimp = right.simplify();

      if(leftSimp.equals(rightSimp))
        return new IntegerOperand(1);

      else if(leftSimp.equals(new IntegerOperand(0)))
        return new IntegerOperand(0);

      else if(leftSimp instanceof IntegerOperand && rightSimp instanceof IntegerOperand) 
        return new IntegerOperand(leftSimp.evaluate(null) / rightSimp.evaluate(null));
      
      else if(rightSimp.equals(new IntegerOperand(1)))
        return leftSimp;

      else 
        return new QuotientExpression(leftSimp,rightSimp); 
   }   
}
