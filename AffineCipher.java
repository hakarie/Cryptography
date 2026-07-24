// Online Java Compiler
// Use this editor to write, compile and run your Java code online

class Main {
    public static void main(String[] args) {
        String PT = "security";
        int a = 7;
        int b = 5;
        
        Main obj = new Main();
        
        String CT = obj.encrypt(PT, a, b);
        System.out.println("Cipher: " + CT);
        
        String DT = obj.decrypt(CT, a, b);
        System.out.println("Decrypted: " + DT);
        
    }
    
    public String encrypt(String PT, int a, int b){
        StringBuilder sb = new StringBuilder();
        
        for(char c : PT.toCharArray()){
            char val = (char) (((a * (c - 'a') + b) % 26) + 'a');
            sb.append(val);
        }
        
        return sb.toString();
    }
    
    public String decrypt(String CT, int a, int b){
        StringBuilder sb = new StringBuilder();
        
        int aInv = 0;
        for(int i = 0; i < 26; i++){
            if((a * i) % 26 == 1){
                aInv = i;
                break;
            }
        }
        
        
        for(char c : CT.toCharArray()){
            char val = (char)  (((((c - 'a') - b + 26) * aInv) % 26) + 'a');
            
            sb.append(val);
        }
        
        return sb.toString();
    }
}
