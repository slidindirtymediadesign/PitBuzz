<?php
require __DIR__.'/bootstrap.php'; $uid=require_user(); $b=body();
$m=db()->prepare("SELECT team_id,role FROM team_members WHERE user_id=? LIMIT 1");$m->execute([$uid]);$manager=$m->fetch();
if(!$manager||!in_array($manager['role'],['owner','admin'],true)) out(['error'=>'Team manager required'],403);
$mid=(int)($b['memberId']??0);$s=db()->prepare("SELECT id FROM team_members WHERE id=? AND team_id=?");$s->execute([$mid,$manager['team_id']]);if(!$s->fetch()) out(['error'=>'Member not found'],404);
$ids=array_values(array_unique(array_map('intval',$b['riderIds']??[])));$pdo=db();$pdo->beginTransaction();
$s=$pdo->prepare("DELETE FROM team_member_riders WHERE member_id=?");$s->execute([$mid]);
$ins=$pdo->prepare("INSERT INTO team_member_riders(member_id,rider_id) SELECT ?,id FROM team_riders WHERE id=? AND team_id=?");
foreach($ids as $rid)$ins->execute([$mid,$rid,$manager['team_id']]);
$s=$pdo->prepare("UPDATE team_members SET follows_all_riders=? WHERE id=?");$s->execute([!empty($b['followsAllRiders'])?1:0,$mid]);$pdo->commit();out(['ok'=>true]);
