<?php
require __DIR__.'/bootstrap.php';$uid=require_user();
if($_SERVER['REQUEST_METHOD']==='GET'){
 $s=db()->prepare("SELECT announcement_autoplay AS announcementAutoplay,notification_sound AS notificationSound,private_message_audio AS privateMessageAudio FROM user_settings WHERE user_id=?");$s->execute([$uid]);out($s->fetch()?:['announcementAutoplay'=>1,'notificationSound'=>1,'privateMessageAudio'=>0]);
}
if($_SERVER['REQUEST_METHOD']==='POST'){
 $b=body();$pm=!empty($b['privateMessageAudio'])?1:0;
 $s=db()->prepare("INSERT INTO user_settings(user_id,announcement_autoplay,notification_sound,private_message_audio) VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE announcement_autoplay=VALUES(announcement_autoplay),notification_sound=VALUES(notification_sound),private_message_audio=VALUES(private_message_audio)");
 $s->execute([$uid,!empty($b['announcementAutoplay'])?1:0,!empty($b['notificationSound'])?1:0,$pm]);out(['ok'=>true]);
}
out(['error'=>'Method not allowed'],405);
