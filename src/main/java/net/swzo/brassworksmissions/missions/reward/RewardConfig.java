package net.swzo.brassworksmissions.missions.reward;

import com.google.gson.annotations.SerializedName;

public class RewardConfig {
    @SerializedName("item")
    private String item;

    public String getItem() {
        return item;
    }
}