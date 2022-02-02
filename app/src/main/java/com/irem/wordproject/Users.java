package com.irem.wordproject;
import java.util.Comparator;

public class Users implements Comparable<Users>{
    private String name;
    private String heart;
    private int score;


    public Users(){}

    public Users(String name, String heart, int score) {
        this.name = name;
        this.heart = heart;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeart() {
        return heart;
    }

    public void setHeart(String heart) {
        this.heart = heart;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Userss{" +
                "name='" + name + '\'' +
                ", heart='" + heart + '\'' +
                ", score='" + score + '\'' +
                '}';
    }

    @Override
    public int compareTo(Users userss) {
        if (this.score>userss.score) return 1;
        else if (this.score < userss.score ) return -1 ;
        else
            return 0;
    }



    public static class Byscore implements Comparator<Users> {

        @Override
        public int compare(Users userss1, Users userss2) {
            return userss1.score < userss2.score ? 1 : userss1.score > userss2.score ? -1 :0;
        }
    }


}