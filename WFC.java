/*
Wave Function Collapse v1 - Joshua Bradley
NOTE: Everything's hard coded right now, but
I'm planning on moving it away from the 3D array
system in later versions
Right now WFC(n, true) is error-ridden, so
I'd recommend using false!
 */
import java.util.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;

public class WFC {
    private int n;
    private ArrayList<Integer>[][] board;
    private boolean[][] collapsed;
    private BufferedImage image;
    private File file;
    String[][][] tiles = {{{" "," "," "}, // 0
                            {" "," "," "},
                            {" "," "," "}},
                            {{" ","x"," "}, // 1
                            {"x","x","x"},
                            {" "," "," "}},
                            {{" ","x"," "}, // 2
                            {"x","x"," "},
                            {" ","x"," "}},
                            {{" "," "," "}, // 3
                            {"x","x","x"},
                            {" ","x"," "}},
                            {{" ","x"," "}, // 4
                            {" ","x","x"},
                            {" ","x"," "}}};
        // stored north,south,east,west
    int[][][] invalidTiles = {{{2,3,4}, {1,2,4}, {1,2,3}, {1,3,4}}, // 0
            {{0,1}, {1,2,4}, {0,4},{0,2}}, // 1
            {{0,1},{0,3},{1,2,3},{0,2}}, // 2
            {{2,3,4},{0,3},{0,4},{0,2}}, // 3
            {{0,1},{0,3},{0,4},{1,3,4}}}; // 4
    // 0-blank, 1-up, 2-left, 3-down, 4-right
    public WFC(int n, boolean flood){
        this.n = n;
        board = new ArrayList[n][n];
        collapsed = new boolean[n][n];
        image = new BufferedImage(n*3, n*3, BufferedImage.TYPE_INT_RGB );
        file = new File("output.png");
        for (int y = 0; y < n*3; y++) {
            for (int x = 0; x < n*3; x++) {
                // Set the pixel colour of the image n.b. x = cc, y = rc //Color.BLACK.getRGB()
                image.setRGB(x, y, Color.BLACK.getRGB());
            }//for cols
        }//for rows
        updateImage();
        // fill board with all possibilities
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                board[i][j] = new ArrayList<Integer>();
                board[i][j].addAll(Arrays.asList(0,1,2,3,4));
            }
        }
        Random rand = new Random();
        int i = rand.nextInt(n);
        int j = rand.nextInt(n);

        if(flood){
            fill(i, j, true);
        } else {
            for(int k = 0; k < board.length; k++){
                for(int l = 0; l < board.length; l++){
                    fill(k,l, false);
                }
            }
        }
    }
    // fill site (i, j), and h determines whether to flood or not
    private void fill(int i, int j, boolean flood) {
        // make sure i and j are valid
        if(i < 0 || i > n-1 || j < 0 || j > n-1) { return; }
        // make sure we haven't already worked on it
        if(collapsed[i][j]) { return; }
        // fill board[i][j] with a potential candidate
        Random rand = new Random();
        int r = board[i][j].get(rand.nextInt(board[i][j].size()));
        board[i][j].clear();
        board[i][j].add(r);
        collapsed[i][j] = true;
        // change the north south east and west values according to r
        // north
        if(i-1 > -1 && board[i-1][j].size() > 1){
            for(int k = 0; k < invalidTiles[r][0].length; k++){
                board[i-1][j].remove(Integer.valueOf(invalidTiles[r][0][k]));
            }
        }
        //south
        if(i+1 < n && board[i+1][j].size() > 1){
            for(int k = 0; k < invalidTiles[r][1].length; k++){
                board[i+1][j].remove(Integer.valueOf(invalidTiles[r][1][k]));
            }
        }
        // east
        if(j+1 < n && board[i][j+1].size() > 1){
            for(int k = 0; k < invalidTiles[r][2].length; k++){
                board[i][j+1].remove(Integer.valueOf(invalidTiles[r][2][k]));
            }
        }
        // west
        if(j-1 > -1 && board[i][j-1].size() > 1){
            for(int k = 0; k < invalidTiles[r][3].length; k++){
                board[i][j-1].remove(Integer.valueOf(invalidTiles[r][3][k]));
            }
        }
        // call fill on north south east and west
        if(flood) {
            fill(i - 1, j, true);
            fill(i + 1, j, true);
            fill(i, j + 1, true);
            fill(i, j - 1, true);
        }
    }
    public void updateImage(){
        try{
            ImageIO.write(image, "png", file);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    public void printBoardSimple(){
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board.length; j++){
                System.out.print(board[i][j].get(0));
            }
            System.out.println();
        }
    }
    public void printBoardAdvanced(){
        for(int i = 0; i < board.length * 3; i++){
            for(int j = 0; j < board.length * 3; j++){
                if(board[i/3][j/3].size() > 1) {
                    System.out.print("o");
                    continue;
                }
                System.out.print(tiles[board[i/3][j/3].get(0)][i%3][j%3]);
            }
            System.out.println();
        }
    }
    public void printImage(){
        for ( int y = 0; y < n*3; y++ ) {
            for ( int x = 0; x < n*3; x++ ) {
                // Set the pixel colour of the image n.b. x = cc, y = rc //Color.BLACK.getRGB()
                int pixel = tiles[board[y/3][x/3].get(0)][y%3][x%3].equals("x") ?
                        Color.BLACK.getRGB() : Color.WHITE.getRGB();
                image.setRGB(x, y, pixel);
                //label.repaint();
            }//for cols
        }//for rows
        updateImage();
    }
    public void printDebug(){
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board.length; j++){
                System.out.print("(");
                for(int k = 0; k < board[i][j].size(); k++) {
                    System.out.print(board[i][j].get(k));
                }
                System.out.print(")");
            }
            System.out.println();
        }
    }
    public static void main(String[] args) {
        // n*n board, and then flood style
        int n = 20;
        WFC wfc = new WFC(n, false);
        wfc.printImage();
    }
}
