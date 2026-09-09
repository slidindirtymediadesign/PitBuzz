<?php
require __DIR__.'/bootstrap.php';
if($_SERVER['REQUEST_METHOD']==='GET'){
 $r=db()->query("SELECT current_race AS currentRace,current_label AS currentLabel,upcoming_race AS upcomingRace,updated_at AS updatedAt FROM race_status WHERE id=1")->fetch();
 out($r?:['currentRace'=>1,'currentLabel'=>'','upcomingRace'=>2]);
}
if($_SERVER['REQUEST_METHOD']==='POST'){
 require_race_admin(); $b=body();
 if(($b['action']??'')==='next') db()->exec("UPDATE race_status SET current_race=current_race+1,upcoming_race=COALESCE(upcoming_race,current_race+1)+1 WHERE id=1");
 elseif(($b['action']??'')==='set'){ $s=db()->prepare("UPDATE race_status SET current_race=?,current_label=?,upcoming_race=? WHERE id=1"); $s->execute([(int)$b['currentRace'],trim($b['currentLabel']??''),isset($b['upcomingRace'])?(int)$b['upcomingRace']:null]); }
 else out(['error'=>'Invalid action'],422);
 $r=db()->query("SELECT current_race AS currentRace,current_label AS currentLabel,upcoming_race AS upcomingRace FROM race_status WHERE id=1")->fetch(); out($r);
}
out(['error'=>'Method not allowed'],405);
