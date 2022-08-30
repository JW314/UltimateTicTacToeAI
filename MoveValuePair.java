class MoveValuePair implements Comparable{
    int moveID;
    float value;

    public MoveValuePair(int m, float v){
        moveID = m;
        value = v;
    }

    @Override
    public int compareTo(Object o) {
        MoveValuePair other = (MoveValuePair) o;


        float diff = (this.value - other.value);
        //i should use an epsilon but no :) this is a bit faster ig than calling some outside epsilon
        if(diff > 0.0000001f) return 1;
        if(diff < -0.0000001f) return -1;

        //this mimics the exact behavior of the compare function prior to compareTo update where -1 was not made
        // equal to infinity...
        //if(this.value > 10000) return other.moveID - this.moveID;
        //else return (diff > 0) ? 1 : -1;

        //...but this code should be pretty much the same performance (despite different moves) and a little bit faster

        return other.moveID - this.moveID;

    }

    @Override
    public String toString() {
        return "M{" +
                "id=" + moveID +
                ", v=" + value +
                '}';
    }
}
