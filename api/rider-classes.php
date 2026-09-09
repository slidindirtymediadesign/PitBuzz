<?php
require __DIR__.'/bootstrap.php'; $uid=require_user(); $b=body();
$m=db()->prepare("SELECT team_id,role FROM team_members WHERE user_id=? LIMIT 1");$m->execute([$uid]);$member=$m->fetch();
if(!$member||!in_array($member['role'],['owner','admin'],true)) out(['error'=>'Team manager required'],403);
$rid=(int)($b['riderId']??0);
$s=db()->prepare("SELECT id FROM team_riders WHERE id=? AND team_id=?");$s->execute([$rid,$member['team_id']]);if(!$s->fetch()) out(['error'=>'Rider not found'],404);
if($_SERVER['REQUEST_METHOD']==='GET'){
 $s=db()->prepare("SELECT c.id,c.name,(rc.rider_id IS NOT NULL) selected FROM racing_classes c LEFT JOIN team_rider_classes rc ON rc.class_id=c.id AND rc.rider_id=? ORDER BY c.name");$s->execute([$rid]);out(['classes'=>$s->fetchAll()]);
}
if($_SERVER['REQUEST_METHOD']==='POST'){
 $ids=array_values(array_unique(array_map('intval',$b['classIds']??[])));$pdo=db();$pdo->beginTransaction();
 $s=$pdo->prepare("DELETE FROM team_rider_classes WHERE rider_id=?");$s->execute([$rid]);
 $ins=$pdo->prepare("INSERT INTO team_rider_classes(rider_id,class_id) VALUES(?,?)");
 foreach($ids as $cid) $ins->execute([$rid,$cid]);
 $pdo->commit();out(['ok'=>true]);
}
out(['error'=>'Method not allowed'],405);
