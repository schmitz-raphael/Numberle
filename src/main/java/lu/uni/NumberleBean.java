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

    public void checkGuess() { 
        // store the attempt in form of a string
        String attempt = "";

        //in order to visualize a number in a single line, a div with a flexbox is used
        attempt += "<div style = 'display:flex'>";
        //loop through the digits of the guess
        for (int i = 0; i < guess.length(); i++) {
            //retrieve the char of the digit and the position of the digit in the secret number
            char guessDigit = guess.charAt(i);
            int digitPosition = getDigitPosition(guessDigit);

            //if a digit is not found in the target number, but the digit in a red box
            if (digitPosition == -1){
                 attempt += "<div class = 'digit' style='background-color:red'>" +guessDigit + "</div>";
            }else if (i < digitPosition && guessDigit != secretNumber.charAt(i)) {
                attempt +="<div class = 'digit' style='background-color:#ffd700'>"+guessDigit+"</div>";
            }else if (i > digitPosition && guessDigit != secretNumber.charAt(i)){
                    attempt += "<div class = 'digit' style='background-color:orange'>" + guessDigit + "</div>";
            } else {
                attempt += "<div class = 'digit' style='background-color:#007FF'>" + guessDigit + "</div>";
            }
        }
        attempt += "</div>";
        attempts += "<div>"+attempt+"</div>";
        tryCounter++;

        //won = guess.equals(secretNumber);
        guess = "";

    }
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

    public int getTryCounter() {
        return tryCounter; // Expose tryCounter if needed for UI logic
    }
}
