from fastapi import FastAPI, Query
from langchain_openai import ChatOpenAI
from langchain.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain_community.chat_message_histories.in_memory import ChatMessageHistory
from langchain_core.chat_history import BaseChatMessageHistory
from langchain_core.runnables.history import RunnableWithMessageHistory
from py_eureka_client import eureka_client
from dotenv import load_dotenv
from urllib import parse

import os

app_name = "gpt"
port = 8003

load_dotenv()
openai_api_key = os.getenv("OPENAI_API_KEY")
eureka_server_url = os.getenv("EUREKA_SERVER_URL")

eureka_client.init(
    eureka_server=eureka_server_url,
    app_name=app_name,
    instance_id=app_name,
    instance_port=port
)

app = FastAPI()

model = ChatOpenAI(
    openai_api_key=openai_api_key,
    temperature=0.1
)

prompt = ChatPromptTemplate.from_messages([
    ("system", "당신은 학생들의 질문을 아주 친절하고 자세하게 답변할 수 있는 AI 챗봇이다."),
    MessagesPlaceholder(variable_name="chat_history"),
    ("human", "{question}")
])

runnable = prompt | model

store = {}


def get_session_history(session_id: str) -> BaseChatMessageHistory:
    if session_id not in store:
        store[session_id] = ChatMessageHistory()
    return store[session_id]


with_message_history = (
    RunnableWithMessageHistory(
        runnable,
        get_session_history,
        input_messages_key="question",
        history_messages_key="chat_history"
    )
)


@app.get("/api/gpt")
async def chat(q: str = Query(...), user_id: int = Query(..., alias="userId")):
    question = parse.unquote(q)

    result = with_message_history.invoke(
        {"question": question},
        config={"configurable": {"session_id": user_id}}
    )

    return {"question": question, "answer": result.content}


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app=app, host="0.0.0.0", port=port)
