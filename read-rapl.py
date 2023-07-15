import os
import time

cpu_rapl_path = '/sys/class/powercap/intel-rapl/intel-rapl:0/energy_uj'

with open(cpu_rapl_path, 'r') as file:
    initial_value = int(file.read().strip())
    
os.system("javac MathServer.java")

os.system("java MathServer")

time.sleep(1)

with open(cpu_rapl_path, 'r') as file:
    final_value = int(file.read().strip())
    
energy_consumption = final_value - initial_value

print("energy consumption (Joules): ", energy_consumption)

#sudo chmod +r /sys/class/powercap/intel-rapl/intel-rapl:0/energy_uj
