# -*- coding: utf-8 -*-
"""
Created on Sun Jun  4 16:29:00 2017

@author: Daniela

Documentation for plt:
https://matplotlib.org/users/pyplot_tutorial.html
"""
import pandas as pd
import matplotlib.pyplot as plt

file_name = 'Metaheuristik'

# Load Data DataFrame
mh_sa = pd.read_csv(file_name+'.csv', sep=';')

# Create Figures
plt.figure(figsize=(8,8))
long01 = plt.plot(mh_sa.Iteration, mh_sa.Long01, label = 'Long01.1')
long012 = plt.plot(mh_sa.Iteration, mh_sa.Long012, label = 'Long01.2')
long02 = plt.plot(mh_sa.Iteration, mh_sa.Long02, label = 'Long02')
long0105 = plt.plot(mh_sa.Iteration, mh_sa.Long0105, label = 'Cooling:0.5')

plt.ylabel('Strafpunkte')
plt.xlabel("Iteration")

plt.setp(long01, color='r', linewidth=2.0)
plt.setp(long012, color='b', linewidth=2.0)
plt.setp(long02, color='g', linewidth=2.0)
plt.setp(long0105, color='y', linewidth=2.0)

plt.legend()
plt.show()

plt.savefig(file_name+'_fig.png', dpi=800)
