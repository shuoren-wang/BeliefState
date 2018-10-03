package com.company;

public class BeliefStateUpdate {
    class EvidenceProbability{
        double oneWall;
        double twoWalls;
        double end;
        EvidenceProbability(double oneWall, double twoWalls, double end){
            this.oneWall = oneWall;
            this.twoWalls = twoWalls;
            this.end = end;
        }
    }
    //UP=0, RIGHT=1, DOWN=2, LEFT=3
    final static private int UP = 0;
    final static private int RIGHT = 1;
    final static private int DOWN = 2;
    final static private int LEFT = 3;
    final static private int END = 0;
    //pre->cur, in the order of up, right, down, left
    private int[] xDir = new int[]{0,1,0,-1};
    private int[] yDir = new int[]{-1,0,1,0};
    private int rowLen = 3;
    private int colLen = 4;
    private double[][] beliefState;
    private EvidenceProbability[][] evidenceProbabilities;
    /**
     *
     * @param action UP=0, RIGHT=1, DOWN=2, LEFT=3
     * @param obs 1wall=1, 2walls=2, end=0
     * @return
     */
    void forward(int action, int obs){
        double[][] preBeliefState = cloneDoubleBeliefState();
        double alpha = 0;
        for(int r=0; r<rowLen;r++){
            for(int c=0; c<colLen;c++){
                if(r==1 && c==1){
                    continue;
                }
                double sumExpPreStates=0;
                //iterate possible pre state, neighbors
                for(int i=0; i<4; i++){
                    int tmpC=c-xDir[i];
                    int tmpR=r-yDir[i];
                    double p;
                    if(c==tmpC+xDir[action] && r==tmpR+yDir[action]){
                        p=0.8;
                    }else if((tmpC-c!=0 && c==tmpC+xDir[action]) ||
                            (tmpR-r!=0 && r==tmpR+yDir[action])){
                        p=0;
                    }else{
                        p=0.1;
                    }
                    if(p!=0 && isTerminal(tmpC,tmpR)){
                        p=0;
                    }

                    double preBeliefStateCell;
                    if (isValidIndex(tmpR, tmpC)) {
                        preBeliefStateCell = preBeliefState[tmpR][tmpC];
                    } else {
                        preBeliefStateCell = preBeliefState[r][c];
                    }
                    sumExpPreStates+=p * preBeliefStateCell;
                }

                beliefState[r][c]=getEvidenceProbability(r,c,obs)*sumExpPreStates;
                alpha+= beliefState[r][c];
            }
        }

        //normalize belief state
        for(int r=0; r<rowLen;r++){
            for(int c=0; c<colLen;c++){
                beliefState[r][c] = beliefState[r][c]/alpha;
            }
        }

        printBeliefStates();
    }


    double[][] cloneDoubleBeliefState(){
        double[][] newBS= new double[rowLen][colLen];
        for(int r=0; r<rowLen; r++){
            newBS[r]= beliefState[r].clone();
        }

        return newBS;
    }

    double getEvidenceProbability(int r, int c, int obs){
        double evidenceProb = 0;
        switch (obs){
            case 2:
                evidenceProb=evidenceProbabilities[r][c].twoWalls;
                break;
            case 1:
                evidenceProb=evidenceProbabilities[r][c].oneWall;
                break;
            default://end
                evidenceProb=evidenceProbabilities[r][c].end;
        }
        return evidenceProb;
    }
    boolean isValidIndex(int r, int c){
        return !(r==1 && c==1)
                && r>=0 && r<rowLen
                && c>=0 && c<colLen;
    }

    /**
     *
     * @param initBeliefState: (2,0)-(2,3),(1,0),(1,3),(0,0)-(0,2)
     * @param actions UP=0, RIGHT=1, DOWN=2, LEFT=3
     * @param observations 1wall=1, 2walls=2, end=0
     * @return
     */
    void updateBeliefState(double[] initBeliefState, int[] actions, int[] observations){
        initBeliefState(initBeliefState);
        int len = actions.length;
        for(int i=0; i<len; i++){
            forward(actions[i], observations[i]);
        }
    }


    void initBeliefState(double[] initBeliefState){
        beliefState = new double[rowLen][colLen];
        int i=0;
        for(int r=rowLen-1; r>=0;r--){
            for(int c=0; c<colLen;c++){
                if((r==1&&c==1) || isTerminal(r,c)){
                    continue;
                }
                beliefState[r][c]=initBeliefState[i];
                i++;
            }
        }
        setupEvidenceProbabilities();
    }

    void setupEvidenceProbabilities(){
        evidenceProbabilities = new EvidenceProbability[rowLen][colLen];
        for(int r=0; r<rowLen; r++){
            for(int c=0; c<colLen; c++){
                if(isTerminal(r,c)){
                    evidenceProbabilities[r][c]= new EvidenceProbability(0,0,1);
                }else if(c==2){
                    evidenceProbabilities[r][c]= new EvidenceProbability(0.9,0.1,0);
                }else{
                    evidenceProbabilities[r][c]= new EvidenceProbability(0.1,0.9,0);
                }
            }
        }
    }

    boolean isTerminal(int r, int c){
        return (r==0 && c==3) || (r==1 && c==3);
    }

    void printBeliefStates(){
        for(int r=0; r<rowLen;r++){
            for(int c=0; c<colLen;c++){
                System.out.printf("  %.8f", beliefState[r][c]);
            }
            System.out.println();
        }
        System.out.println();
    }


    /**
     * initBeliefState: (2,0)-(2,3),(1,0),(1,3),(0,0)-(0,2)
     * actions UP=0, RIGHT=1, DOWN=2, LEFT=3
     * observations 1wall=1, 2walls=2, end=0
     * @param args
     */
    public static void main(String[] args) {
	// write your code here
        double[] initBeliefState = new double[]{0.111,0.111,0.111,0.111,0.111,0.111,0.111,0.111,0.111};
        int[] actions, obs;

//        BeliefStateUpdate m1 = new BeliefStateUpdate();
//        actions=new int[]{UP,UP,UP};
//        obs = new int[]{2,2,2};
//        m1.updateBeliefState(initBeliefState, actions, obs);
//        m1.printBeliefStates();
//
//        BeliefStateUpdate m2 = new BeliefStateUpdate();
//        actions=new int[]{UP,UP,UP};
//        obs = new int[]{1,1,1};
//        m2.updateBeliefState(initBeliefState, actions, obs);
//        m2.printBeliefStates();
//
//        initBeliefState = new double[]{0,0,0,0,0,0,0,1,0};
//        BeliefStateUpdate m3 = new BeliefStateUpdate();
//        actions=new int[]{RIGHT,RIGHT,UP};
//        obs = new int[]{1,1,END};
////        actions=new int[]{RIGHT,RIGHT};
////        obs = new int[]{1,1};
//        m3.updateBeliefState(initBeliefState, actions, obs);
//        m3.printBeliefStates();

        initBeliefState = new double[]{1,0,0,0,0,0,0,0,0};
        BeliefStateUpdate m4 = new BeliefStateUpdate();
        actions=new int[]{UP,RIGHT,RIGHT,RIGHT};
        obs = new int[]{2,2,1,1};
        m4.updateBeliefState(initBeliefState, actions, obs);
        m4.printBeliefStates();
    }

    //todo, check length of actions, obs are equal;
    //todo check length of initBeliefState == 9
}
