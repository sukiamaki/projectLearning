
public class Othello {
	private int row = 6;	//オセロ盤の縦横マス数(2の倍数のみ)
	private String [] grids = new String [(row+2) * (row+2)]; //局面情報
	private String turn = "black"; //手番
	
	private boolean give_up = false; //投了情報
	private boolean opp_give_up = false; //相手の投了情報
	
	private StringBuilder changeStone; //ひっくり返った石の位置
	
	// コンストラクタ
	public Othello(){
		turn = "black"; //黒が先手
		for(int i = 0 ; i < (row+2) * (row+2) ; i++){
			grids[i] = "wall"; //盤面を一周している壁
		}
		
		for(int j=1; j<=row; j++) {
			for(int k=1; k<=row; k++) {
				grids[j*(row+2) + k]= "board";	//盤面
			}
		}
		
		//石の初期配置
		int center = (row+2) * (row+2) / 2;
		grids[center - (row + 2) / 2 - 1] = "black";
		grids[center + (row + 2) / 2    ] = "black";
		grids[center - (row + 2) / 2    ] = "white";
		grids[center + (row + 2) / 2 - 1] = "white";
			
	}

	// メソッド
	
	
	/////////////////*手番情報の取得*/////////////////
	public void setTurn(String color){ 
		turn = color;
	}
	
	/////////////////*手番情報の確認*/////////////////
	public String getTurn(){ 
		return turn;
	}

	
	//////////////////////*局面の変更*//////////////////////
	public boolean putStone(int i, String color, boolean effect_on){ //effect_on：実際にひっくり返すかどうか
		boolean try_putStone = false;
		
		//石が置いてある場所に置いた時
		if(grids[i].equals("white") || grids[i].equals("black")) {
			return try_putStone;
		}
		
		//石が置いてないところに置いた時
		//置かれた石の8方向においてチェック位置をずらして色を調べる
		int count;	//現在のチェック位置
		changeStone = new StringBuilder();
		
		int [] distance = {-1, 1, -(row+2), row+2, -(row+3), row+3, -(row+1), row+1};		//移動距離

		/* ４２６
		   ０●１
		   ７３５		distance[]*/
		
		for(int dis: distance) { //8方向において試す
			count = dis; //チェック位置をずらす
			while(grids[i+count].equals(changeColor(color)) ) {	//置いた石と違う色が続く間実行
				count += dis;	//チェック位置をずらす
			
				if(grids[i+count].equals(color)) {	//置いた石と同じ色の石が出現したら
					try_putStone = true;	//石置ける！
					
					if(effect_on == true) {	//局面の変更
						while(count!=0) {	//今までチェックしてきた石の色を変更(ひっくり返す)
							grids[i+count] = color;
							changeStone.append("," + (i+count));
							count -= dis;
						}	
						grids[i] = color;	//置いた石の位置に色付け
						changeStone.append("," + i);
					}
				}
			}
		}		
		return try_putStone;
	}
		
	////////*局面情報の変更(ひっくり返す)*//////	
	public void setGrids(int i,String color){
		grids[i] = color;
	}

	/////////*変化のあった局面情報(ひっくり返った石の位置)の確認*//////////
	public String getChangeStone() {
		return changeStone.toString();
	}
	
	//////////////*局面情報の確認*//////////////
	public String [] getGrids(){ // 局面情報を取得
		return grids;
	}
	
	
	
	////////////////*色(手番)を変更*//////////////
	public String changeColor(String color){ 
		if(color.equals("black")) {
			return "white";
		}else {
			return "black";
		}
	}
	
	////////////*投了状態に変更*///////////
	public void changeGiveUp(String player) {
		if(player.equals("me")) {
			give_up = true;
		}else {
			opp_give_up = true;
		}
	}
	
	//////////////*対局終了を判断(石が埋まっているか)*/////////////////////
	public boolean isGameover(){	
		//投了されているか
		if(give_up == true || opp_give_up == true) {
			return true;
		}
					
		//盤面埋まっているか
		for(int i=0;i<(row+2)*(row+2);i++) {
			if(grids[i].equals("board")) {
				return false;
			}
		}	
		return true;
	}
	
	//////////////盤面上で置ける場所があるかどうか*///////////////
	public boolean checkBoard(String color) {	
		for(int i=1; i<=row; i++) {
			for(int j=1; j<=row; j++) {
				if(putStone((i*(row +2) + j), color, false) == true) {
					return true;
				};
			}
		}
		return false;
	}

	/////////////////*勝敗の判断*/////////////////
	public String checkWinner(String color){	
		int white_num=0;	//白い石の数
		int black_num=0;//黒い石の数
		
		if(give_up == false && opp_give_up == false) {
			for(int i=0; i<(row+2)*(row+2); i++) {
				if(grids[i] == "white") {
					white_num++;
				}else if(grids[i] == "black") {
					black_num++;
				}
			}
		
			if(white_num > black_num) {
				return "whiteの勝ち";	
			}else if(white_num < black_num) {
				return "blackの勝ち";
			}else {
				return "引き分け";
			}
		}
		else if(give_up == true){
			return "投了により" + changeColor(color) + "の勝ち";
		}else if(opp_give_up == true) {
			return "相手の投了により" + color + "の勝ち";	
		}
		
		return "勝敗判断で原因不明のエラーが発生しました。";
	}
	
	/////////////*縦横のマス数を取得*/////////////
	public int getRow(){ 
		return row;
	}
}
