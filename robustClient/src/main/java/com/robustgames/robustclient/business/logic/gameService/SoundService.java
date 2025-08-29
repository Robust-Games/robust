/**
 * @author Nico Steiner
 */
package com.robustgames.robustclient.business.logic.gameService;

import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.dsl.FXGL;

public class SoundService {
    //    int lastNumber = 0;

    public static void pickSong() {
        String[] playlist = new String[]{
                "Deadly_Contracts.mp3",
                "Final_Solitaire.mp3",
                "Going_Undercover.mp3",
                "Hostile_Territory.mp3",
                "Micron_By_Micron.mp3",
                "Shadow_Operations.mp3",
                "The_Price_of_Freedom.mp3"
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
