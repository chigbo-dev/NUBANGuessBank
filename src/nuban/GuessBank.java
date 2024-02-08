package nuban;

import java.util.List;
import java.util.Scanner;

public class GuessBank {

    public static void main(String[] args) throws ClassNotFoundException {
        System.out.println("Enter account number");
        String ac_no = (new Scanner(System.in)).nextLine();
        
        List<String> likelyBanks = NUBAN.guessBank(ac_no);
    }
    
}
