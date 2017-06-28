# -*- coding: utf-8 -*-
"""
Created on Sun Jun  4 16:29:00 2017

@author: Daniela

Documentation for plt:
https://matplotlib.org/users/pyplot_tutorial.html
"""
import pandas as pd
import matplotlib.pyplot as plt

file_name = 'output_new'

# Load Data DataFrame
mh_sa = pd.read_csv(file_name+'.csv', sep=',')

# Ziel: Darstellung des Scores in Abhängigkeit der Cooling Rate und Starttemperatur (jeweils was sich ändert)
# insgesamt 5 Grafiken
# Startemperatur konstant
#Dateiname = list(set(mh_sa.Dateiname.tolist()))

Dateiname = ['toy1', 'long01','long_hidden01',  'long_hint01','long_late01',
             'medium01', 'medium_hidden01','medium_hint01','medium_late01','sprint01',
             'sprint_hidden01','sprint_hint01','sprint_late01']
# Anzeige für Dateidurchlaufen
i = 0
j = 10


for dateiname in Dateiname:
    
    #### Für feste Starttemperatur
    plt.figure(figsize=(15,4))
    a = 141
    b = 1
    
    while(b<4): 
        plt.subplot(a)
        grafik = plt.plot(mh_sa.CoolingRate[i:j], mh_sa.Score[i:j], label = [mh_sa.Dateiname[i], mh_sa.StartTemperatur[i]])
        plt.setp(grafik, color='r', linewidth = 2.0)
        plt.ylabel('Score')
        plt.xlabel("CoolingRate")
        plt.ylim(mh_sa[mh_sa.Dateiname == dateiname].Score.min(),mh_sa[mh_sa.Dateiname == dateiname].Score.max())
        plt.legend()
        i = j+10
        j = j+20
        a = a+1
        b = b+1
        plt.gca().invert_xaxis()
      
    plt.savefig(dateiname+'1_fig.png', dpi=800)
    plt.show()
    
    plt.figure(figsize=(15,4))
    a = 141
    b = 1
    
    while(b<4): 
        
        plt.subplot(a)
        grafik = plt.plot(mh_sa.CoolingRate[i:j], mh_sa.Score[i:j], label = [mh_sa.Dateiname[i], mh_sa.StartTemperatur[i]])
        plt.setp(grafik, color='r', linewidth = 2.0)
        plt.ylabel('Score')
        plt.xlabel("CoolingRate")
        plt.ylim(mh_sa[mh_sa.Dateiname == dateiname].Score.min(),mh_sa[mh_sa.Dateiname == dateiname].Score.max())
        plt.legend()
        i = j+10
        j = j+20
        a = a+1
        b = b+1
        plt.gca().invert_xaxis()
    
    plt.savefig(dateiname+'2_fig.png', dpi=800)
    plt.show()
    
    
    
    ##########################################################################
    # Cooling Rate fest, Starttemperatur variabel
i = 10
j = 20
for dateiname in Dateiname:
    
    plt.figure(figsize=(15,4))
    a = 141
    b = 1 
    
    while(b<4): 
        
        plt.subplot(a)
        grafik = plt.plot(mh_sa.StartTemperatur[i:j], mh_sa.Score[i:j], label = [mh_sa.Dateiname[i], mh_sa.CoolingRate[i]])
        plt.setp(grafik, color='r', linewidth = 2.0)
        
        plt.ylabel('Score')
        plt.xlabel("StartTemperatur")
        plt.ylim(mh_sa[mh_sa.Dateiname == dateiname].Score.min(),mh_sa[mh_sa.Dateiname == dateiname].Score.max())
        plt.legend()
        i = j+10
        j = j+20
        a = a+1
        b = b+1
        plt.gca().invert_xaxis()
    
    plt.savefig(dateiname+'ST_fig.png', dpi=800)
    plt.show()
    
    plt.figure(figsize=(15,4))
    a = 141
    b = 1
    
    while(b<4):
        
        plt.subplot(a)
        grafik = plt.plot(mh_sa.StartTemperatur[i:j], mh_sa.Score[i:j], label = [mh_sa.Dateiname[i], mh_sa.CoolingRate[i]])
        plt.setp(grafik, color='r', linewidth = 2.0)
        plt.ylabel('Score')
        plt.xlabel("StartTemperatur")
        plt.ylim(mh_sa[mh_sa.Dateiname == dateiname].Score.min(),mh_sa[mh_sa.Dateiname == dateiname].Score.max())
        plt.legend()
        i = j+10
        j = j+20
        a = a+1
        b = b+1
        plt.gca().invert_xaxis()
    
    plt.savefig(dateiname+'ST2_fig.png', dpi=800)
    plt.show()
    
##############################################################################
##############################################################################     
"""
Für Long01
"""
  

file_name = 'output2'

file_name = 'long_hidden01_output'
# Load Data DataFrame
mh_sa = pd.read_csv(file_name+'.csv', sep=',')

Dateiname = ['toy1', 'long01','long_hidden01',  'long_hint01','long_late01',
             'medium01', 'medium_hidden01','medium_hint01','medium_late01','sprint01',
             'sprint_hidden01','sprint_hint01','sprint_late01']

for dateiname in Dateiname:
    
    file_name = dateiname+'_output'
    mh_sa = pd.read_csv(file_name+'.csv', sep=',')
    
    #####
    # für konstante Cooling Rate
    i = 10
    j = 20
        
    plt.figure(figsize=(20,4))
    a = 161
    b = 1 
    
    while(b<7): 
        
        plt.subplot(a)
        grafik = plt.plot(mh_sa.StartTemperatur[i:j], mh_sa.Score[i:j], label = ['CoolingRate:', mh_sa.CoolingRate[i]])
        plt.setp(grafik, color='r', linewidth = 2.0)
        
        plt.ylabel('Score')
        plt.xlabel("StartTemperatur")
        plt.ylim(mh_sa[mh_sa.Dateiname == dateiname].Score.min(),mh_sa[mh_sa.Dateiname == dateiname].Score.max())
        plt.legend()
        i = j+10
        j = j+20
        a = a+1
        b = b+1
        plt.gca().invert_xaxis()
    
    plt.savefig(str(dateiname)+str(i)+'ST_fig.png', dpi=800)
    plt.show()
    
        
        ##########################################################################
        #
    i = 0
    j = 10
    
    
        
    #### Für feste Starttemperatur
    plt.figure(figsize=(20,4))
    a = 161
    b = 1
    
    while(b<7): 
        plt.subplot(a)
        grafik = plt.plot(mh_sa.CoolingRate[i:j], mh_sa.Score[i:j], label = ['Starttemperatur:', mh_sa.StartTemperatur[i]])
        plt.setp(grafik, color='r', linewidth = 2.0)
        plt.ylabel('Score')
        plt.xlabel("CoolingRate")
        plt.ylim(mh_sa[mh_sa.Dateiname == dateiname].Score.min(),mh_sa[mh_sa.Dateiname == dateiname].Score.max())
        plt.legend()
        i = j+10
        j = j+20
        a = a+1
        b = b+1
        plt.gca().invert_xaxis()
      
    plt.savefig(str(dateiname)+str(i)+'1_fig.png', dpi=800)
    plt.show()