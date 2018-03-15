package com.mab.homeguardsecurities;

/**
 * Created by MonisBana on 12/31/2017.
 */

public class Post {
    private String PostTitle;
    private String PostDesc;
    private String Image;
    private Long VoteCount;
    private String Name;
    private String Date;
    public Post(){

    }
    public Post(String postTitle,String postDesc,String image,Long VoteCount,String Name,String Date){
        this.PostTitle = postTitle;
        this.PostDesc = postDesc;
        this.Image = image;
        this.VoteCount =VoteCount;
        this.Name = Name;
        this.Date = Date;
    }

    public String getPostTitle() {
        return PostTitle;
    }

    public void setPostTitle(String postTitle) {
        this.PostTitle = postTitle;
    }

    public String getPostDesc() {
        return PostDesc;
    }

    public void setPostDesc(String postDesc) {
        this.PostDesc = postDesc;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        this.Image = image;
    }

    public Long getVoteCount() {
        return VoteCount;
    }

    public void setVoteCount(Long voteCount) {
        VoteCount = voteCount;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
