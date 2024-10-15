package lu.uni;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

@Named("numberleBean")
@SessionScoped
public class NumberleBean implements Serializable {
    private static final Logger logger = Logger.getLogger(NumberleBean.class.getName());

    private List<String> numbersList;
    private String secretNumber;
    private String guess;
    private String feedback;
    private String attempts;
    private int tryCounter = 0;

    @PostConstruct
    public void init() {
        loadNumbers();
        selectRandomNumber();
        this.attempts = "";
        this.tryCounter = 0;
    }

    private void loadNumbers() {
        numbersList = new ArrayList<>();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("numbers.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {

            String line;
            while ((line = reader.readLine()) != null) {
                numbersList.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void selectRandomNumber() {
        Random rand = new Random();
        this.secretNumber = numbersList.get(rand.nextInt(numbersList.size()));
        logger.info(secretNumber);
    }
    /**
     * small function to check that every character in the guess is a digit
     * returns false as soon as anything other than a digit is encountered 
     */ 
    public boolean isValidGuess(){
        for (int i = 0; i < guess.length(); i++){
            char ch = guess.charAt(i);
            if (!Character.isDigit(ch)) return false;
        }
        return true;
    }
    public String checkGuess() {
        //if a guess is not complete immediately exit the function
        if (guess.length() < 6 || !isValidGuess()) return "index";
        // store the attempt in form of a string
        String attempt = "";
        //in order to visualize a number in a single line, a div with a flexbox is used
        attempt += "<div style = 'display:flex'>";
        //loop through the digits of the guess
        for (int i = 0; i < guess.length(); i++) {
            //retrieve the char of the digit and the position of the digit in the secret number
            char guessDigit = guess.charAt(i);
            int digitPosition = getDigitPosition(guessDigit);

            //if a digit is not found in the target number, put the digit in a red box
            if (digitPosition == -1){
                 attempt += "<div class = 'digit' style='background-color:red'>" +guessDigit + "</div>";
            }
            //when the digit is to the left  of its actual position --> blue box
            else if (i < digitPosition && guessDigit != secretNumber.charAt(i)) {
                attempt +="<div class = 'digit' style='background-color:blue'>"+guessDigit+"</div>";
            }
            //when the digit is to the right of its actual position --> orange box
            else if (i > digitPosition && guessDigit != secretNumber.charAt(i)){
                    attempt += "<div class = 'digit' style='background-color:orange'>" + guessDigit + "</div>";
            }
            // when the digit is at the right position --> green box
            else {
                attempt += "<div class = 'digit' style='background-color:green'>" + guessDigit + "</div>";
            }
        }
        attempt += "</div>";
        //put the attempt in its own div to make sure that every attempt is in another line and add it to the attempts
        attempts += "<div>"+attempt+"</div>";
        tryCounter++;
        if (tryCounter >= 6) {
            return "failure";  // Trigger the navigation rule
        }
        if (guess.equals(secretNumber)) return "success";
        guess = "";
        return "index"; // Stay on the same page if the game isn't over
    }
    /**
     * 
     * @param ch
     * @return position of the digit in the secret number
     * @return -1 if the digit is not found
     */
    private int getDigitPosition(char ch){
        for (int i = 0; i < secretNumber.length(); i++){
            if (ch == secretNumber.charAt(i)){
                return i;
            }
        }
        return -1;
    }

    // Getters and Setters
    public String getGuess() {
        return guess;
    }

    public void setGuess(String guess) {
        this.guess = guess;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getAttempts() {
        return attempts; // Return the array directly for easy access in UI
    }
    public String getSecretNumber(){
        return secretNumber;
    }
    public int getTryCounter() {
        return tryCounter;
    }
    public String resetGame(){
        init();
        return "index";
    }
}
