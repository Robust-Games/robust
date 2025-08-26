/**
 * @author Nico Steiner
 */
package com.robustgames.robustclient.business.logic.gameService;

import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.dsl.FXGL;

public class SoundService {
    //    int lastNumber = 0;

    public static void pickSong(){
        String[] playlist = new String[]{
                "Deadly Contracts.mp3",
                "Final Solitaire.mp3",
                "Going Undercover.mp3",
                "Hostile Territory.mp3",
                "Micron By Micron.mp3",
                "Shadow Operations.mp3",
                "The Price of Freedom.mp3"
        };

       int pick = (int) (Math.random() * (playlist.length));
 /*        while (pick == lastNumber){
            pick = randomNumber();
        }*/
        FXGL.getAudioPlayer().stopAllMusic();
        Music pickedMusic = FXGL.getAssetLoader().loadMusic(playlist[pick]);
        FXGL.getAudioPlayer().loopMusic(pickedMusic);
    }
}
