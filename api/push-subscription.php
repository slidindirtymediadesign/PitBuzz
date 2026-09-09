<?php
require __DIR__.'/bootstrap.php';$uid=require_user();
if($_SERVER['REQUEST_METHOD']!=='POST')out(['error'=>'Method not allowed'],405);
$b=body();$platform=$b['platform']??'';$provider=trim($b['provider']??'');$key=trim($b['deviceKey']??'');$sub=$b['subscription']??null;
if(!in_array($platform,['web','android','ios'],true)||$provider===''||$key===''||!is_array($sub))out(['error'=>'Invalid subscription'],422);
$s=db()->prepare("INSERT INTO push_subscriptions(user_id,platform,provider,device_key,subscription_json,enabled) VALUES(?,?,?,?,?,1) ON DUPLICATE KEY UPDATE user_id=VALUES(user_id),platform=VALUES(platform),subscription_json=VALUES(subscription_json),enabled=1");
$s->execute([$uid,$platform,$provider,$key,json_encode($sub)]);out(['ok'=>true]);
