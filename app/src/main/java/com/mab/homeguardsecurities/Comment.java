package com.mab.homeguardsecurities;

/**
 * Created by MonisBana on 12/31/2017.
 */

public class Comment {
    private String Name;
    private String Comment;
    private Long VoteCount;
    private String Date;
    public Comment (){

    }
    public Comment(String Name,String Comment,Long Like,String Date){
        this.Name = Name;
        this.Comment = Comment;
        this.VoteCount = Like;
        this.Date = Date;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public Long getVoteCount() {
        return VoteCount;
    }

    public void setVoteCount(Long voteCount) {
        VoteCount = voteCount;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
