package zeus.quantm.greenfood.utils;

/**
 * Created by EDGY on 6/25/2017.
 */

public class ValidateInput {
    private static ValidateInput validateInput;

    public static ValidateInput getInstance(){
        if(validateInput == null){
            validateInput = new ValidateInput();
        }
        return validateInput;
    }

    public static String checkStringLength(String inputString, String message){
        return inputString.length() == 0 ? message : "";
    }

}
