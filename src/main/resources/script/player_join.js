//javaのクラスをインポート
var NPCData = CLASS_NPCData.static;

/**
 * プレイヤーが参加してSQLからセーブデータのロードが完了した直後に呼び出される関数
 * @param ShellCasePlayer shellCasePlayer 
 * @param Player bukkitPlayer 
 * @param KurokoPlayer kurokoPlayer
 */
function onPlayerJoin(shellCasePlayer, bukkitPlayer, kurokoPlayer){
    
    //クエストの進捗を取得
    var questProgress = shellCasePlayer.getQuestProgress();

    //QuestFlagの0番がfalseである場合にNPCをスポーン
    if(!questProgress.getFlag(0)) kurokoPlayer.addNPC(NPCData.getNPCData("example-data"));

}