import pandas as pd
import matplotlib.pyplot as plt
import numpy as np


# Load the CSV file
file_path = 'lf1445530.csv'  # Replace with your file path
data = pd.read_csv(file_path,  names=["TimePoint" ,"Ld","Rd", "Lx" ,"Ly" ,"Rx" ,"Ry" ,"leftSpeed", "rightSpeed" , "lightValue"])

# Create the plot
plt.figure(figsize=(10, 10))



mid_x = []
mid_y = []
full_x = []
full_y = []

# Iterate through the data to find the midpoints for lightValue < 400
for _, row in data.iterrows():
    full_x.append((row['Lx'] + row['Rx']) / 2)
    full_y.append((row['Ly'] + row['Ry']) / 2)
    if row['lightValue'] < 10:
        mid_x.append((row['Lx'] + row['Rx']) / 2)
        mid_y.append((row['Ly'] + row['Ry']) / 2)


# Plot the midpoints and the black line connecting them
plt.scatter(mid_x, mid_y, color='black', label='black location')
#plt.plot(mid_x, mid_y, color='', linewidth=5, label='Line Path')
plt.plot(full_x, full_y, color='gray', linestyle='dashed' , label='Drive Path')

# Plot Lx, Ly and Rx, Ry points
plt.scatter(data['Lx'], data['Ly'], color='red' )
plt.scatter(data['Rx'], data['Ry'], color='green')

plt.plot(data['Lx'], data['Ly'], color='red', label='Left Path')
plt.plot(data['Rx'], data['Ry'], color='green', label='Right Path')



# Add labels and legend
plt.xlabel('X')
plt.ylabel('Y')
plt.legend()
plt.title('Robot Track')
plt.grid(True)

# Show the plot
plt.savefig(file_path+".png")
plt.close()
