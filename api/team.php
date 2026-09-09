<?php
require __DIR__.'/bootstrap.php'; $uid=require_user();
$m=db()->prepare("SELECT * FROM team_members WHERE user_id=? LIMIT 1"); $m->execute([$uid]); $membership=$m->fetch();
if($_SERVER['REQUEST_METHOD']==='GET'){
 if(!$membership) out(['team'=>null,'riders'=>[],'members'=>[],'canManage'=>false]);
 $tid=(int)$membership['team_id'];
 $s=db()->prepare("SELECT * FROM teams WHERE id=?");$s->execute([$tid]);$team=$s->fetch();
 $s=db()->prepare("SELECT * FROM team_riders WHERE team_id=? ORDER BY id");$s->execute([$tid]);$riders=$s->fetchAll();
 $s=db()->prepare("SELECT id,name,phone,role,follows_all_riders AS followsAllRiders FROM team_members WHERE team_id=? ORDER BY id");$s->execute([$tid]);$members=$s->fetchAll();
 out(['team'=>$team,'riders'=>$riders,'members'=>$members,'canManage'=>in_array($membership['role'],['owner','admin'],true)]);
}
if($_SERVER['REQUEST_METHOD']==='POST'){
 $b=body(); $action=$b['action']??'';
 if($action==='create_team'){
   if($membership) out(['error'=>'Already belongs to a team'],409);
   $pdo=db();$pdo->beginTransaction();$s=$pdo->prepare("INSERT INTO teams(name,created_by) VALUES(?,?)");$s->execute([trim($b['name']??'My Team'),$uid]);$tid=(int)$pdo->lastInsertId();
   $s=$pdo->prepare("INSERT INTO team_members(team_id,user_id,name,role) SELECT ?,id,name,'owner' FROM users WHERE id=?");$s->execute([$tid,$uid]);$pdo->commit();out(['ok'=>true,'teamId'=>$tid],201);
 }
 if(!$membership||!in_array($membership['role'],['owner','admin'],true)) out(['error'=>'Team manager required'],403);
 $tid=(int)$membership['team_id'];
 if($action==='add_rider'){ $s=db()->prepare("INSERT INTO team_riders(team_id,name,race_number,is_managed) VALUES(?,?,?,1)");$s->execute([$tid,trim($b['name']??''),trim($b['raceNumber']??'')?:null]);out(['ok'=>true,'riderId'=>(int)db()->lastInsertId()],201); }
 if($action==='add_member'){ $role=in_array($b['role']??'family',['family','crew'],true)?$b['role']:'family';$s=db()->prepare("INSERT INTO team_members(team_id,name,phone,role,follows_all_riders) VALUES(?,?,?,?,?)");$s->execute([$tid,trim($b['name']??''),trim($b['phone']??'')?:null,$role,!empty($b['followsAllRiders'])?1:0]);out(['ok'=>true,'memberId'=>(int)db()->lastInsertId()],201); }
 out(['error'=>'Unsupported action'],422);
}
out(['error'=>'Method not allowed'],405);
