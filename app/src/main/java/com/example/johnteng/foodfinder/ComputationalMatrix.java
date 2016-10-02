package com.example.johnteng.foodfinder;


import java.util.HashMap;
import com.example.johnteng.foodfinder.personality;
/**
 * Created by Brandon on 9/18/2016.
 */
public class ComputationalMatrix {
    private  personality p ;

    HashMap<String, Double> priceMap = new HashMap<>();
    HashMap<String, Double> ratingMap = new HashMap<>();
    HashMap<String, Double> reviewsMap = new HashMap<>();
    HashMap<String, Double> radiusMap = new HashMap<>();

    public ComputationalMatrix(){
        populateHashmap(priceMap, 0, 0.25, 0, 0, 0, 0, 0, 0, 0.25, 0, 0, 0.25, 0, 0, 0, 0, 0, 0 ,0 ,0.25, 0, 0);
        populateHashmap(ratingMap, 0, 0, 0, 0, 0, 0, 0, 0, 0.333, 0, 0, 0, 0, 0, 0.333, 0, 0, 0 ,0 ,0.333, 0, 0);
        populateHashmap(reviewsMap, 0, 0, 0.333, 0, 0, 0.333, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ,0 ,0, 0, 0.333);
        populateHashmap(radiusMap, 0, 0, 0.167, 0, 0.167, 0, 0, 0.167, 0, 0, 0, 0.167, 0, 0, 0, 0.167, 0, 0 ,0 ,0, 0, 0.167);
    }

    public void populateHashmap(HashMap<String, Double> hashmap, double a, double b, double c, double d, double e, double f, double g, double h, double i, double j, double k, double l, double m, double n, double o, double p, double q, double r, double s, double t, double u, double v){
        hashmap.put("Agreeableness", a);
        hashmap.put("Conscientiousness", b);
        hashmap.put("Extraversion", c);
        hashmap.put("Emotional Range", d);
        hashmap.put("Openness", e);
        hashmap.put("Excitement", f);
        hashmap.put("Harmony", g);
        hashmap.put("Curiosity", h);
        hashmap.put("Ideal", i);
        hashmap.put("Closeness", j);
        hashmap.put("Self-expression", k);
        hashmap.put("Liberty", l);
        hashmap.put("Love", m);
        hashmap.put("Practicality", n);
        hashmap.put("Stability", o);
        hashmap.put("Challenge", p);
        hashmap.put("Structure", q);
        hashmap.put("Helping others", r);
        hashmap.put("Tradition", s);
        hashmap.put("Hedonism", t);
        hashmap.put("Achieving success", u);
        hashmap.put("Open to change", v);
    }

    public double returnCalculationFactor(HashMap<String, Double> hashmap){
        return   hashmap.get("Agreeableness")*p.agreeableness+
                hashmap.get("Conscientiousness")*p.conscientiousness+
                hashmap.get("Extraversion")*p.extraversion+
                hashmap.get("Emotional Range")*p.emotionalRange+
                hashmap.get("Openness")*p.openness+
                hashmap.get("Excitement")*p.excitement+
                hashmap.get("Harmony")*p.harmony+
                hashmap.get("Curiosity")*p.curiosity+
                hashmap.get("Ideal")*p.ideal+
                hashmap.get("Closeness")*p.closeness+
                hashmap.get("Self-expression")*p.selfExpression+
                hashmap.get("Liberty")*p.liberty+
                hashmap.get("Love")*p.love+
                hashmap.get("Practicality")*p.practicality+
                hashmap.get("Stability")*p.stability+
                hashmap.get("Challenge")*p.challenge+
                hashmap.get("Structure")*p.structure+
                hashmap.get("Helping others")*p.helpingOthers+
                hashmap.get("Tradition")*p.tradition+
                hashmap.get("Hedonism")*p.hedonism+
                hashmap.get("Achieving success")*p.achievingSuccess+
                hashmap.get("Open to change")*p.opennessToChange;

    }

    public double maxGenerator(HashMap<String, Double> hashmap){
        return   hashmap.get("Agreeableness")+
                hashmap.get("Conscientiousness")+
                hashmap.get("Extraversion")+
                hashmap.get("Emotional Range")+
                hashmap.get("Openness")+
                hashmap.get("Excitement")+
                hashmap.get("Harmony")+
                hashmap.get("Curiosity")+
                hashmap.get("Ideal")+
                hashmap.get("Closeness")+
                hashmap.get("Self-expression")+
                hashmap.get("Liberty")+
                hashmap.get("Love")+
                hashmap.get("Practicality")+
                hashmap.get("Stability")+
                hashmap.get("Challenge")+
                hashmap.get("Structure")+
                hashmap.get("Helping others")+
                hashmap.get("Tradition")+
                hashmap.get("Hedonism")+
                hashmap.get("Achieving success")+
                hashmap.get("Open to change");
    }

    public String  calculateStandardForPrice(HashMap<String, Double> bb){
        double standard = returnCalculationFactor(bb)/maxGenerator(bb);
        if(0<standard || standard<30){
            //return "1,2"; //John might not be able to parse this kind of format, need to check
        }

        else if(30<standard || standard<70){
            //return "1,2,3";
        }

        else if(30<standard || standard<70) {
            //return "1,2,3,4";
        }
        return "1";
        //return "1,2,3,4";   // this case is actually error
    }

    public int calculateStandardForRadius(HashMap<String, Double> bb){
        double standar = returnCalculationFactor(bb)/maxGenerator(bb);
        if(0<standar || standar<30){
            return 1000; //John might not be able to parse this kind of format, need to check
        }

        else if(30<standar || standar<70){
            return 5000;
        }

        else if(30<standar || standar<70) {
            return 10000;
        }
        return 0;   // this case is actually error
    }
    public double  calculateRatingWeight(HashMap<String, Double> bb){
        double standard = returnCalculationFactor(bb)/maxGenerator(bb);
        return standard;
    }
    public double  calculateReviewWeight(HashMap<String, Double> bb){
        double standard = returnCalculationFactor(bb)/maxGenerator(bb);
        return standard;
    }

    public String choseSortingMethod () {
        double ratingWeight = this.calculateRatingWeight(this.ratingMap);
        double reviewWeight = this.calculateReviewWeight(this.reviewsMap);
        if (ratingWeight > reviewWeight && Math.abs(ratingWeight - reviewWeight) > 10)
            return "rating";
        else if (ratingWeight > reviewWeight && Math.abs(reviewWeight - ratingWeight) > 10)
            return "review_count";
        else
            return "best_match";
    }
}
