public class HashKey {
    int[] state;
    int hash;

    public HashKey(int[] s, int h){
        Tester.hashStateCopy -= Tester.getTesterTime();
        state = new int[s.length];
        for (int i = 0; i < s.length; i++) {
            state[i] = s[i];
        }
        hash = h;
        Tester.hashStateCopy += Tester.getTesterTime();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashKey hashKey = (HashKey) o;
        if(hashKey.state.length != this.state.length) return false;
        for (int i = 0; i < state.length; i++) {
            if(state[i] != hashKey.state[i]) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
