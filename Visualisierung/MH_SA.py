# -*- coding: utf-8 -*-
"""
Created on Sun Jun  4 16:29:00 2017

@author: Daniela

Documentation for plt:
https://matplotlib.org/users/pyplot_tutorial.html
"""
import pandas as pd
import matplotlib.pyplot as plt

file_name = 'output'

# Load Data DataFrame
mh_sa = pd.read_csv(file_name+'.csv', sep=',')

# Ziel: Darstellung des Scores in Abhängigkeit der Cooling Rate und Starttemperatur (jeweils was sich ändert)
# insgesamt 5 Grafiken
# Startemperatur konstant
Dateiname = list(set(mh_sa.Dateiname.tolist()))

for dateiname in Dateiname:
    
    #### Für feste Starttemperatur
    i = 0
    j = 10
    plt.figure(figsize=(15,4))
    a = 151
    b = 1
    
    while(b<6): # j<100
        
        #plt.figure(figsize=(4,4))
        plt.subplot(a)
        #plt.set_ybound(20,23)
        grafik = plt.plot(mh_sa.CoolingRate[i:j], mh_sa.Score[i:j], label = [mh_sa.Dateiname[i], mh_sa.StartTemperatur[i]])
        plt.setp(grafik, color='r', linewidth = 2.0)
        #toy1 = plt.plot(mh_sa.Iteration, mh_sa.Long01, label = 'Long01.1')
        
        plt.ylabel('Score')
        plt.xlabel("CoolingRate")
        plt.ylim(mh_sa[mh_sa.Dateiname == dateiname].Score.min(),mh_sa[mh_sa.Dateiname == dateiname].Score.max())
        plt.legend()
        i = j+10
        j = j+20
        a = a+1
        b = b+1
    
    plt.show()
    #plt.savefig(file_name+'_fig.png', dpi=800)
    
    # Cooling Rate konstant
    """ 
    i = 10
    j = 20
    while(j <= len(mh_sa)): # j<100
        plt.figure(figsize=(4,4))
         
        grafik = plt.plot(mh_sa.StartTemperatur[i:j], mh_sa.Score[i:j], label = [mh_sa.Dateiname[i], mh_sa.CoolingRate[i]])
        plt.setp(grafik, color='r', linewidth = 2.0)
        #toy1 = plt.plot(mh_sa.Iteration, mh_sa.Long01, label = 'Long01.1')
        
        plt.ylabel('Score')
        plt.xlabel("CoolingRate")
        
        plt.legend()
        plt.show()
        i = j+10
        j = j+20
       """ 

#mh_sa[mh_sa.Dateiname == 'toy1'].describe()

