class Main {
    public static void main(String[] args) {
        Main obj = new Main();
        
        int a = 11;
        int b = 26;
        
        System.out.println("gcd(" + a + ", " + b + ") = " + obj.gcd(a, b));
    }
    
    public int gcd(int a, int b){
        if(b == 0){
            return a;
        }
        
        return gcd(b , a % b);
    }
}
