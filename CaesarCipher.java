
class CaesarCipher {
      
    public String encrypt(String word, int key){
        char[] cipher = word.toCharArray();
        
        for(int i = 0; i < cipher.length; i++){
            if(Character.isLetterOrDigit(cipher[i])){
                if(Character.isUpperCase(cipher[i])){
                    cipher[i] = (char) ((((cipher[i] - 'A') + 3) % 26) + 'A');
                } else if(Character.isLowerCase(cipher[i])) {
                    cipher[i] = (char) ((((cipher[i] - 'a') + 3) % 26) + 'a');
                } else {
                    cipher[i] = (char) ((((cipher[i] - '0') + 3) % 10) + '0');
                }
            }
        }
        
        return new String(cipher);
    }
    
    public String decrypt(String cipher, int key){
        char[] word = cipher.toCharArray();
        
        for(int i = 0; i < word.length; i++){
            if(Character.isLetterOrDigit(word[i])){
                if(Character.isUpperCase(word[i])){
                    word[i] = (char) ((((word[i] - 'A') - 3) % 26) + 'A');
                } else if(Character.isLowerCase(word[i])) {
                    word[i] = (char) ((((word[i] - 'a') - 3) % 26) + 'a');
                } else {
                    word[i] = (char) ((((word[i] - '0') - 3) % 10) + '0');
                }
            }
        }
        
        return new String(word);
    }

}