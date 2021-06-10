package com.example.demo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationRegex {
    public static boolean isRegexEmail(String target) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }
    public static boolean isRegexInteger(String target){
        String regex = "^[0-9]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public static boolean isRegexEngNumSpecialCharCombinationPwd(String pwd){
        String regex = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,20}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(pwd);
        return matcher.find();
    }
    public static boolean isRegexEngNumCombinationPwd(String pwd){
        String regex = "^[A-Za-z[0-9]]{8,20}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(pwd);
        return matcher.find();
    }
    public static boolean isRegexNumSpecialCharCombinationPwd(String pwd){
        String regex = "^[[0-9]$@$!%*#?&]{8,20}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(pwd);
        return matcher.find();
    }
    public static boolean isRegexEngSpecialCharCombinationPwd(String pwd){
        String regex = "^[[A-Za-z]$@$!%*#?&]{8,20}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(pwd);
        return matcher.find();
    }
    public static boolean isRegexSamePwd(String pwd){
        String regex = "(\\w)\\1\\1";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(pwd);
        return matcher.find();
    }
    public static boolean isContinuousPwd(String pwd) {
        int o = 0;
        int d = 0;
        int p = 0;
        int n = 0;
        int limit = 3;

        for(int i=0; i<pwd.length(); i++) {
            char tempVal = pwd.charAt(i);
            if(i > 0 && (p = o - tempVal) > -2 && (n = p == d ? n + 1 :0) > limit -3) {
                return true;
            }
            d = p;
            o = tempVal;
        }
        return false;
    }
    public static boolean isContainEmailId(String pwd, String email){
        int idx = email.indexOf("@");
        String emailId = email.substring(0, idx);
        if(pwd.contains(emailId)){
            return true;
        }
        return false;
    }
    public static boolean isRegexName(String name){
        String regex = "^[ㄱ-ㅎ가-힣a-zA-Z]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);
        return matcher.find();
    }
    public static boolean isRegexPhone(String phone){
        String regex = "^01(?:0|1|[6-9])(\\d{3}|\\d{4})(\\d{4})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phone);
        return matcher.find();
    }
}

