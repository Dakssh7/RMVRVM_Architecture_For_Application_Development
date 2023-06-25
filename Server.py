from fastapi import FastAPI
import asyncio
import concurrent.futures
import math

app = FastAPI()

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

    return {"result": result}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)

# You can test the server by sending a GET request to http://localhost:8000/calculate/{number} where {number} is the integer you want to perform the math calculation on. The server will return a JSON response with the result.
