**Anarko - Android**
===========

Anarko is a product of Oppous LLC.

Co-Founders
===========
Jesus Materano (CTO), Luis Palomo (COO)

v 1.0
----

Requirements
----------
[API (Routes)](https://docs.google.com/spreadsheets/d/1edcjmmeLUGbPtrsQhwCaC0lhP3a75RURcXR0ER37oKA/edit?usp=sharing)


Usage
----------
Camera Social App

[sample video](https://drive.google.com/file/d/0B1xsQKNLPt2HUkEzMjJscWhSdDA/view)


    
Project test mode:
------------------
    you can test parts of project by changing varient i in AKMainActivity.java.

        //Team working branches
        int i = 3; // 1:peter 2:Wang 3:combine
        Intent intent;

        if (i == 1) {
            intent = new Intent(this, SettingsMainActivity.class);
        } else if (i == 2) {
			intent = new Intent(this, CamCaptureActivity.class);
		} else{
            intent = new Intent(this, RegTourActivity.class);
        }

Credits
--------
Jesus Materano(Project manager)\n
Erik Narbona(API/Backend Developer)\n
Peter(Android developer), Wang Jian(Android developer)\n
Gerardo Leon (Graphic designer)

