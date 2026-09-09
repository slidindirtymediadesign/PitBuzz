<?php
require __DIR__.'/bootstrap.php'; $uid=require_user();
if($_SERVER['REQUEST_METHOD']==='GET'){
 $s=db()->prepare("SELECT m.id,m.body,m.created_at AS createdAt,m.read_at AS readAt,m.sender_user_id AS senderUserId,m.recipient_user_id AS recipientUserId,u.name AS senderName FROM private_messages m JOIN users u ON u.id=m.sender_user_id WHERE m.sender_user_id=? OR m.recipient_user_id=? ORDER BY m.created_at DESC LIMIT 100");$s->execute([$uid,$uid]);out(['messages'=>$s->fetchAll()]);
}
if($_SERVER['REQUEST_METHOD']==='POST'){
 $b=body();$to=(int)($b['recipientUserId']??0);$text=trim($b['body']??'');if(!$to||$text==='')out(['error'=>'Recipient and message required'],422);
 $s=db()->prepare("INSERT INTO private_messages(sender_user_id,recipient_user_id,body) VALUES(?,?,?)");$s->execute([$uid,$to,$text]);out(['ok'=>true,'messageId'=>(int)db()->lastInsertId(),'privateAudioAutoPlay'=>false],201);
}
out(['error'=>'Method not allowed'],405);
