public class DSSlidingWindow {

    int capacity;

    int itemCount;
    int updateIndex;
    float[] values;
    float curSum;
    int lifetimeItemCount;
    float lifetimeSum;
    float lifetimeMax;

    public DSSlidingWindow(int size){
        capacity = size;
        itemCount = 0;
        updateIndex = 0;
        values = new float[capacity];
        curSum = 0;
        lifetimeSum = 0;
        lifetimeMax = 0;
    }

    public void reset(){
        itemCount = 0;
        updateIndex = 0;
        values = new float[capacity];
        curSum = 0;
        lifetimeSum = 0;
        lifetimeItemCount = 0;
    }

    public void addItem(float val){
        if(itemCount == 0) lifetimeMax = val;
        else lifetimeMax = Math.max(lifetimeMax, val);
        //keep track of how many items are being held/have been entered
        if(itemCount < capacity) itemCount++;
        lifetimeItemCount++;

        //replace sliding out value with new value: update sums accordingly
        curSum -= values[updateIndex];
        values[updateIndex] = val;
        curSum += values[updateIndex];
        lifetimeSum += values[updateIndex];

        //move target to the next item in array
        updateIndex++;
        while(updateIndex >= capacity) updateIndex -= capacity;
    }

    public float getWindowAverage(){
        return curSum/itemCount;
    }
    public float getLifetimeAverage(){
        return lifetimeSum/lifetimeItemCount;
    }
    public float getLifetimeMax(){ return lifetimeMax; }
}
