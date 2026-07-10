class Main {
    public static void main(String[] args) {
        Main obj = new Main();
        
        int a = -12;
        int b = 26;
        
        System.out.println("is (" + a + ", " + b + ") co-prime = " + (obj.gcd(a, b) == 1));
    }
    
    public int gcd(int a, int b){
        if(b == 0){
            return a;
        }
        
        return gcd(b , a % b);
    }
}





























