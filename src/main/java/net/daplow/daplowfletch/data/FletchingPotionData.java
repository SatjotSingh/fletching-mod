package net.daplow.daplowfletch.data;

public class FletchingPotionData {

    // Default values for Potion Values
    private String potionID = "minecraft:water";
    private int potionLeft = 0;
    private int potionColor = 0xFF385DC6;;

    public FletchingPotionData() {}

    public FletchingPotionData(String potionID, int potionLeft, int potionColor) {
        this.potionID = potionID;
        this.potionLeft = potionLeft;
        this.potionColor = potionColor;
    }



    public String getPotionID() {
        return potionID;
    }

    public int getPotionColor() {
        return potionColor;
    }

    public void setPotionID(String potionID) {
        this.potionID = potionID;
    }

    public void setPotionColor(int potionColor) {
        this.potionColor = potionColor;
    }

    public int getPotionLeft() {
        return potionLeft;
    }

    public void setPotionLeft(int potionLeft) {
        this.potionLeft = potionLeft;
    }
}

