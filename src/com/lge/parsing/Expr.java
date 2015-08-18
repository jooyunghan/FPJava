package com.lge.parsing;

public abstract class Expr implements Show {
    /** constructors */
    public static Expr con(int n) {
        return new Con(n);
    }
    
    public static Expr bin(Op op, Expr left, Expr right) {
        return new Bin(op, left, right);
    }

    @Override
    public String show() {
        return shows().apply("");
    }
    protected abstract ShowS shows();
    
    /** internals */
    
    private static class Con extends Expr {
        public final int n;

        public Con(int n) {
            this.n = n;
        }
        
        @Override
        public ShowS shows() {
            return ShowS.showString(Integer.toString(n));
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + n;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Con other = (Con) obj;
            if (n != other.n) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "Con(" + n + ")";
        }
        
    }
    
    public static class Bin extends Expr {
        public final Op op;
        public final Expr left;
        public final Expr right;

        public Bin(Op op, Expr left, Expr right) {
            this.op = op;
            this.left = left;
            this.right = right;
        }
        
        @Override
        public ShowS shows() {
            ShowS showSpace = ShowS.showChar(' ');
            return ShowS.showParen(true, left.shows().compose(showSpace).compose(showsop()).compose(showSpace).compose(right.shows()));
        }

        private ShowS showsop() {
            switch (op) {
            case PLUS: return ShowS.showChar('+');
            case MINUS: return ShowS.showChar('-');
            case MUL: return ShowS.showChar('*');
            case DIV: return ShowS.showChar('/');
            }
            throw new IllegalArgumentException(op + " is not handled.");
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((left == null) ? 0 : left.hashCode());
            result = prime * result + ((op == null) ? 0 : op.hashCode());
            result = prime * result + ((right == null) ? 0 : right.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Bin other = (Bin) obj;
            if (left == null) {
                if (other.left != null) {
                    return false;
                }
            } else if (!left.equals(other.left)) {
                return false;
            }
            if (op != other.op) {
                return false;
            }
            if (right == null) {
                if (other.right != null) {
                    return false;
                }
            } else if (!right.equals(other.right)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "Bin(" + op + "," + left + "," + right + ")";
        }
    }
}
