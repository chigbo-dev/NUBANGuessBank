package nuban;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NUBAN {
    static String sp_ex = "\\s";
    
    public static List guessBank(String ac_no) throws ClassNotFoundException {
        List<String> codes = getList();
        List<String> likely = new ArrayList<>();
        codes.stream().filter((code) -> (Integer.valueOf(ac_no.substring(9))==genCheckDigit(code,ac_no.substring(0,9)))).forEachOrdered((code) -> {
            likely.add(code);
        });
        return getPBanks(likely);
    }
    
    private static List<String> getList() throws ClassNotFoundException {
        List<String> list = new ArrayList<>();
        try(Connection conn = dbConnect()){
            String sql = "SELECT guesscode AS code FROM banklist WHERE type = '0'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while(rs.next()){
                list.add(rs.getString("code"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(NUBAN.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
    
    public static String getBankCode(String bankName) throws ClassNotFoundException {
        String bc = "000023";
        try(Connection conn = dbConnect()){
            String sql = "SELECT bankcode FROM banklist WHERE bankname = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bankName);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                bc = rs.getString("bankcode");
            }
        } catch (SQLException ex) {
            Logger.getLogger(NUBAN.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bc;
    }
    
    public static String getAliasCode(String bankName) throws ClassNotFoundException {
        String bc = "101";
        try(Connection conn = dbConnect()){
            String sql = "SELECT * FROM banklist WHERE bankname = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bankName);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                bc = rs.getString("guesscode");
            }
        } catch (SQLException ex) {
            Logger.getLogger(NUBAN.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bc;
    }
    
    public static List<String> guessByName(String bankPrefix) throws ClassNotFoundException {
        List<String> bList = new ArrayList();
        try(Connection conn = dbConnect()){
            String sql = "SELECT * FROM banklist WHERE bankname LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%"+bankPrefix+"%");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                String bn = rs.getString("bankname");
                bList.add(bn);
            }
        } catch (SQLException ex) {
            Logger.getLogger(NUBAN.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bList;
    }
    
    private static List<String> getPBanks(List<String> codes) throws ClassNotFoundException {
        List<String> list = new ArrayList<>();
        try(Connection conn = dbConnect()){
            String c_a[] = new String[codes.size()];
            String sql = "SELECT bankname FROM banklist WHERE guesscode IN ("+addPlaceHolder(codes.size())+")";
            PreparedStatement stmt = conn.prepareStatement(sql);
            for(int i=0;i<codes.size();i++){
                stmt.setString(i+1, codes.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            
            while(rs.next()){
                list.add(rs.getString("bankname"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(NUBAN.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
    
    private static int genCheckDigit(String code, String serial){
        String as = code+serial;
        String ss = "373373373373373";
        int tot=0;
        for(int i=0;i<ss.length();i++){
            int pew = Integer.parseInt(as.charAt(i)+"")*Integer.parseInt(ss.charAt(i)+"");
            tot = tot + pew;
        }
        int rem = tot%10;
        int cd = 10-rem;
        if(cd==10){
            return 0;
        }else{
            return cd;
        }
    }
    
    private static String addPlaceHolder(int size){
        StringBuilder sb = new StringBuilder();
        do{
            if(size==0){
                sb.append("''");
                break;
            }
            sb.append("?,");
            size--;
        }while (size>0);
        if(sb.length()!=0 && sb.charAt(sb.length()-1)==',')sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    private static Connection dbConnect() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
