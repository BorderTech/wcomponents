package com.github.openborders.wcomponents.examples; 

import com.github.openborders.wcomponents.Audio;
import com.github.openborders.wcomponents.AudioResource;
import com.github.openborders.wcomponents.WAudio;
import com.github.openborders.wcomponents.WPanel;

/** 
 * An example showing the basic use of the {@link WAudio} component.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class WAudioExample extends WPanel
{
    /** Creates a WAudioExample. */
    public WAudioExample()
    {
        Audio[] audioClips =  
        {
             new AudioResource("/audio/ogg.ogg", "Ogg audio file"),
             new AudioResource("/audio/flac.flac", "FLAC audio file"),
             new AudioResource("/audio/mp3.mp3", "MPEG3 audio file"),
             new AudioResource("/audio/wav.wav", "Wav audio file"),
             new AudioResource("/audio/au.au", "Sun audio file")
        };
        
        WAudio audio = new WAudio(audioClips);
        audio.setAltText("Example WAudio content");
        add(audio);
    }
}
