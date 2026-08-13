package com.shatteredpixel.shatteredpixeldungeon.scenes;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
class LedgerButton extends StyledButton{
 LedgerButton(Chrome.Type type,String label,int size){super(type,label,size);remove(text);text=new RenderedTextBlock(label,size,false);add(text);layout();}
}
