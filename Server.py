from fastapi import FastAPI
import asyncio
import concurrent.futures
import math
import os
import time


cpu_rapl_path = '/sys/class/powercap/intel-rapl/intel-rapl:0/energy_uj'
start_time
initial_value

app = FastAPI()

@app.get("/")
def greetings():
    global start_time, initial_value
    
    start_time = time.time()

    with open(cpu_rapl_path, 'r') as file:
        initial_value = int(file.read().strip())
    return {"Measuring Started From Home Page"}


def perform_math_calculation(number):
    t = number

    art = math.tan(t)
    atan_art = math.atan(art)
    atan2_t_art = math.atan2(t, art)
    atan2_art_t = math.atan2(art, t)

    return {
        "art": art,
        "atan_art": atan_art,
        "atan2_t_art": atan2_t_art,
        "atan2_art_t": atan2_art_t
    }

@app.get("/calculate/{number}")
async def calculate(number: int):
    loop = asyncio.get_event_loop()

    # Execute the math calculation asynchronously using a thread pool executor
    with concurrent.futures.ThreadPoolExecutor() as pool:
        result = await loop.run_in_executor(pool, perform_math_calculation, number)

    with open(cpu_rapl_path, 'r') as file:
        final_value = int(file.read().strip())

    # Record final timestamp
    end_time = time.time()

    energy_consumption = final_value - initial_value
    execution_time = end_time - start_time


    return {"result: ": result,
            "Energy consumption (microjoules (uJ)): " : energy_consumption,
            "Execution time (seconds): " : execution_time
            }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)

# Test the server by sending a GET request to http://localhost:8000/calculate/{number} where {number} is the integer you want to perform the math calculation on. The server will return a JSON response with the result.
