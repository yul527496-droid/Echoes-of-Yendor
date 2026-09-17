package com.linyu.spdmodifier;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class MainActivity extends Activity {
    private static final String GAME_PACKAGE = "com.shatteredpixel.shatteredpixeldungeon";
    private static final String GAME_FILES = "/data/user/0/" + GAME_PACKAGE + "/files";
    private static final int SPD_400_VERSION = 912;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final List<String> saves = new ArrayList<>();
    private Spinner saveSpinner;
    private TextView rootStatus;
    private TextView saveInfo;
    private Button saveButton;
    private EditText hp, ht, str, level, exp, gold, energy;
    private LinearLayout fieldsContainer;
    private JSONObject currentJson;
    private String currentSave;

    @Override protected void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); buildUi(); testRootAndScan(); }
    @Override protected void onDestroy() { executor.shutdownNow(); super.onDestroy(); }
    private int dp(int v){ return Math.round(v * getResources().getDisplayMetrics().density); }
    private TextView text(String value,int size,boolean bold){ TextView t=new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(0xFF202124); if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD); t.setPadding(0,dp(5),0,dp(5)); return t; }
    private Button button(String title){ Button b=new Button(this); b.setText(title); b.setAllCaps(false); return b; }
    private EditText numberField(String label){ LinearLayout row=new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL); row.setGravity(Gravity.CENTER_VERTICAL); row.setPadding(0,dp(3),0,dp(3)); TextView name=text(label,16,false); row.addView(name,new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1f)); EditText field=new EditText(this); field.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED); field.setGravity(Gravity.END); field.setSingleLine(true); field.setMinWidth(dp(130)); row.addView(field,new LinearLayout.LayoutParams(dp(150),LinearLayout.LayoutParams.WRAP_CONTENT)); fieldsContainer.addView(row); return field; }

    private void buildUi(){
        ScrollView scroll=new ScrollView(this); LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(20),dp(18),dp(20),dp(28)); scroll.addView(root);
        root.addView(text("SPD Modifier",28,true)); root.addView(text("Shattered Pixel Dungeon 4.0.0 · Root 存档修改器",14,false));
        rootStatus=text("Root：检测中…",15,true); root.addView(rootStatus);
        root.addView(text("存档",20,true)); LinearLayout saveRow=new LinearLayout(this); saveRow.setOrientation(LinearLayout.HORIZONTAL); saveRow.setGravity(Gravity.CENTER_VERTICAL); saveSpinner=new Spinner(this); saveRow.addView(saveSpinner,new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1f)); Button refresh=button("刷新"); refresh.setOnClickListener(v->scanSaves()); saveRow.addView(refresh); root.addView(saveRow);
        Button read=button("读取所选存档"); read.setOnClickListener(v->loadSelectedSave()); root.addView(read);
        saveInfo=text("尚未读取存档",14,false); root.addView(saveInfo);
        root.addView(text("角色 / 资源",20,true)); fieldsContainer=new LinearLayout(this); fieldsContainer.setOrientation(LinearLayout.VERTICAL); root.addView(fieldsContainer);
        hp=numberField("当前生命 HP"); ht=numberField("最大生命 HT"); str=numberField("力量 STR"); level=numberField("等级"); exp=numberField("经验"); gold=numberField("金币"); energy=numberField("炼金能量");
        saveButton=button("保存修改（自动备份）"); saveButton.setEnabled(false); saveButton.setOnClickListener(v->saveChanges()); root.addView(saveButton);
        Button restore=button("恢复该存档的修改器备份"); restore.setOnClickListener(v->restoreBackup()); root.addView(restore);
        Button launch=button("启动破碎的像素地牢"); launch.setOnClickListener(v->launchGame()); root.addView(launch);
        TextView notice=text("说明：仅支持官方 4.0.0（存档 version=912）写入。旧版本存档可以读取查看，但不会允许保存。保存前会强制停止游戏并创建 game.dat.spdmod.bak。",13,false); notice.setPadding(0,dp(18),0,0); root.addView(notice);
        setContentView(scroll);
    }

    private void testRootAndScan(){ executor.execute(()->{ RootResult rr=root("id"); boolean ok=rr.code==0&&rr.out.contains("uid=0"); runOnUiThread(()->rootStatus.setText(ok?"Root：已授权 ✓":"Root：未授权 / su 不可用")); if(ok)scanSaves(); }); }
    private void scanSaves(){ executor.execute(()->{ RootResult rr=root("for d in "+sh(GAME_FILES+"/game")+"*; do [ -d \"$d\" ] && basename \"$d\"; done"); List<String> found=new ArrayList<>(); if(rr.code==0){ for(String line:rr.out.split("\\r?\\n")){ line=line.trim(); if(line.matches("game[0-9]+"))found.add(line); } } Collections.sort(found,(a,b)->Integer.compare(slotNumber(a),slotNumber(b))); runOnUiThread(()->{ saves.clear(); saves.addAll(found); ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,saves); adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); saveSpinner.setAdapter(adapter); saveInfo.setText(saves.isEmpty()?"没有检测到 game1 / game2 … 存档。请确认游戏已安装且 Root 已授权。":"已检测到 "+saves.size()+" 个存档槽。请选择后读取。"); }); }); }
    private int slotNumber(String s){ try{return Integer.parseInt(s.substring(4));}catch(Exception e){return 9999;} }

    private void loadSelectedSave(){ Object selected=saveSpinner.getSelectedItem(); if(selected==null){toast("没有可读取的存档");return;} String save=selected.toString(); saveInfo.setText("正在读取 "+save+" …"); saveButton.setEnabled(false); executor.execute(()->{ try{ File local=new File(getCacheDir(),save+"_game.dat"); if(!local.exists()&&!local.createNewFile())throw new Exception("无法创建临时文件"); local.setReadable(true,false); local.setWritable(true,false); String src=GAME_FILES+"/"+save+"/game.dat"; RootResult cp=root("cat "+sh(src)+" > "+sh(local.getAbsolutePath())); if(cp.code!=0||local.length()==0)throw new Exception("Root 读取失败："+cp.err); JSONObject json=readBundle(local); JSONObject hero=json.getJSONObject("hero"); int version=json.optInt("version",-1); int depth=json.optInt("depth",-1); String heroClass=hero.optString("class","UNKNOWN"); int lvl=hero.optInt("lvl",0); currentJson=json; currentSave=save; runOnUiThread(()->{ hp.setText(String.valueOf(hero.optInt("HP",0))); ht.setText(String.valueOf(hero.optInt("HT",0))); str.setText(String.valueOf(hero.optInt("STR",0))); level.setText(String.valueOf(hero.optInt("lvl",0))); exp.setText(String.valueOf(hero.optInt("exp",0))); gold.setText(String.valueOf(json.optInt("gold",0))); energy.setText(String.valueOf(json.optInt("energy",0))); boolean supported=version==SPD_400_VERSION; saveButton.setEnabled(supported); saveInfo.setText(save+" · "+className(heroClass)+" Lv."+lvl+" · 第 "+depth+" 层 · 存档版本 "+version+(supported?" · SPD 4.0.0 ✓":" · 非 4.0.0，仅查看")); }); }catch(Exception e){ runOnUiThread(()->{ saveButton.setEnabled(false); saveInfo.setText("读取失败："+e.getMessage()); }); } }); }

    private String className(String cls){ switch(cls){ case "WARRIOR":return "战士"; case "MAGE":return "法师"; case "ROGUE":return "盗贼"; case "HUNTRESS":return "女猎手"; case "DUELIST":return "决斗家"; case "CLERIC":return "牧师"; default:return cls; } }

    private void saveChanges(){ if(currentJson==null||currentSave==null){toast("请先读取存档");return;} final String sHp=hp.getText().toString().trim(), sHt=ht.getText().toString().trim(), sStr=str.getText().toString().trim(), sLevel=level.getText().toString().trim(), sExp=exp.getText().toString().trim(), sGold=gold.getText().toString().trim(), sEnergy=energy.getText().toString().trim(); saveButton.setEnabled(false); saveInfo.setText("正在保存并校验…"); executor.execute(()->{ try{ if(currentJson.optInt("version",-1)!=SPD_400_VERSION)throw new Exception("只允许写入 version=912 的 4.0.0 存档"); JSONObject hero=currentJson.getJSONObject("hero"); hero.put("HP",parse(sHp,"HP")); hero.put("HT",parse(sHt,"HT")); hero.put("STR",parse(sStr,"STR")); hero.put("lvl",parse(sLevel,"等级")); hero.put("exp",parse(sExp,"经验")); currentJson.put("gold",parse(sGold,"金币")); currentJson.put("energy",parse(sEnergy,"炼金能量")); File out=new File(getCacheDir(),currentSave+"_game_modified.dat"); writeBundle(out,currentJson); JSONObject check=readBundle(out); if(check.optInt("version",-1)!=SPD_400_VERSION)throw new Exception("本地校验失败"); String gameDat=GAME_FILES+"/"+currentSave+"/game.dat"; String backup=gameDat+".spdmod.bak"; RootResult rr=root("am force-stop "+GAME_PACKAGE+"; cp -p "+sh(gameDat)+" "+sh(backup)+" && cat "+sh(out.getAbsolutePath())+" > "+sh(gameDat)); if(rr.code!=0)throw new Exception("Root 写入失败："+rr.err); File verify=new File(getCacheDir(),currentSave+"_verify.dat"); if(!verify.exists())verify.createNewFile(); verify.setReadable(true,false); verify.setWritable(true,false); RootResult vr=root("cat "+sh(gameDat)+" > "+sh(verify.getAbsolutePath())); if(vr.code!=0)throw new Exception("写入后读取校验失败"); JSONObject verified=readBundle(verify); if(verified.optInt("version",-1)!=SPD_400_VERSION)throw new Exception("写入后 JSON/GZIP 校验失败"); runOnUiThread(()->{ saveInfo.setText(currentSave+" 保存成功 ✓ 已自动创建 game.dat.spdmod.bak"); saveButton.setEnabled(true); toast("修改已写入"); }); }catch(Exception e){ runOnUiThread(()->{ saveInfo.setText("保存失败："+e.getMessage()); saveButton.setEnabled(currentJson!=null&&currentJson.optInt("version",-1)==SPD_400_VERSION); }); } }); }
    private int parse(String s,String label)throws Exception{ if(s==null||s.isEmpty())throw new Exception(label+" 不能为空"); long v=Long.parseLong(s); if(v<0||v>Integer.MAX_VALUE)throw new Exception(label+" 超出范围"); return (int)v; }

    private void restoreBackup(){ Object selected=saveSpinner.getSelectedItem(); if(selected==null){toast("没有选择存档");return;} String save=selected.toString(); saveInfo.setText("正在恢复备份…"); executor.execute(()->{ String gameDat=GAME_FILES+"/"+save+"/game.dat"; String backup=gameDat+".spdmod.bak"; RootResult rr=root("[ -s "+sh(backup)+" ] || exit 3; am force-stop "+GAME_PACKAGE+"; cat "+sh(backup)+" > "+sh(gameDat)); runOnUiThread(()->{ if(rr.code==0){saveInfo.setText(save+" 已恢复修改器备份 ✓");toast("备份已恢复");} else if(rr.code==3)saveInfo.setText(save+" 没有 game.dat.spdmod.bak 备份"); else saveInfo.setText("恢复失败："+rr.err); }); }); }
    private void launchGame(){ Intent intent=getPackageManager().getLaunchIntentForPackage(GAME_PACKAGE); if(intent==null){toast("未找到破碎的像素地牢");return;} intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(intent); }

    private JSONObject readBundle(File file)throws Exception{ InputStream base=new BufferedInputStream(new FileInputStream(file)); base.mark(2); int a=base.read(),b=base.read(); base.reset(); InputStream in=(a==0x1f&&b==0x8b)?new GZIPInputStream(base):base; InputStreamReader reader=new InputStreamReader(in,StandardCharsets.UTF_8); StringBuilder sb=new StringBuilder(); char[] buf=new char[8192]; int n; while((n=reader.read(buf))>=0)sb.append(buf,0,n); reader.close(); return new JSONObject(sb.toString()); }
    private void writeBundle(File file,JSONObject json)throws Exception{ GZIPOutputStream gzip=new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(file))); OutputStreamWriter writer=new OutputStreamWriter(gzip,StandardCharsets.UTF_8); writer.write(json.toString()); writer.close(); }
    private RootResult root(String command){ Process process=null; try{ process=Runtime.getRuntime().exec(new String[]{"su","-c",command}); ByteArrayOutputStream stdout=new ByteArrayOutputStream(),stderr=new ByteArrayOutputStream(); Thread t1=pump(process.getInputStream(),stdout),t2=pump(process.getErrorStream(),stderr); int code=process.waitFor(); t1.join(1000);t2.join(1000); return new RootResult(code,stdout.toString("UTF-8"),stderr.toString("UTF-8")); }catch(Exception e){ return new RootResult(-1,"",e.toString()); }finally{ if(process!=null)process.destroy(); } }
    private Thread pump(InputStream in,ByteArrayOutputStream out){ Thread t=new Thread(()->{ try{ byte[] buf=new byte[4096]; int n; while((n=in.read(buf))>=0)out.write(buf,0,n); }catch(Exception ignored){} }); t.start(); return t; }
    private String sh(String s){ return "'"+s.replace("'","'\\''")+"'"; }
    private void toast(String s){ runOnUiThread(()->Toast.makeText(this,s,Toast.LENGTH_SHORT).show()); }
    private static class RootResult{ final int code; final String out,err; RootResult(int code,String out,String err){this.code=code;this.out=out==null?"":out.trim();this.err=err==null?"":err.trim();} }
}
