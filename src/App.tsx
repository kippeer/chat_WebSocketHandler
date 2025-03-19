import React, { useState, useEffect, useRef } from 'react';
import { format } from 'date-fns';
import { IoMdSend } from 'react-icons/io';

interface Message {
  id: string;
  sender: string;
  content: string;
  timestamp: string;
  type: 'CHAT' | 'JOIN' | 'LEAVE';
}

function App() {
  const [messages, setMessages] = useState<Message[]>([]);
  const [inputMessage, setInputMessage] = useState('');
  const [usernameInput, setUsernameInput] = useState('');
  const [username, setUsername] = useState('');
  const [isConnected, setIsConnected] = useState(false);
  const socketRef = useRef<WebSocket | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  // Scroll automático para a última mensagem
  useEffect(() => {
    setTimeout(() => {
      messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, 50);
  }, [messages]);

  // Conexão com o WebSocket
  useEffect(() => {
    if (!username) return;

    const socket = new WebSocket('ws://localhost:8080/chat');
    socketRef.current = socket;

    socket.onopen = () => {
      setIsConnected(true);
      console.log('✅ WebSocket Connected');
    };

    socket.onmessage = (event) => {
      console.log('📩 Received message:', event.data);  // Log para verificar o dado recebido
      try {
        const message: Message = JSON.parse(event.data);
        console.log('📩 Parsed message:', message);  // Verifique o formato da mensagem

        // Verifica se a mensagem tem os campos obrigatórios
        if (message.id && message.sender && message.content) {
          setMessages((prev) => [...prev, message]);
        }
      } catch (error) {
        console.error('❌ Error parsing message:', error);
      }
    };

    socket.onclose = () => {
      console.log('❌ WebSocket Disconnected');
      if (socketRef.current) {
        setIsConnected(false);
        socketRef.current = null;
      }
    };

    socket.onerror = (error) => {
      console.error('⚠️ WebSocket Error:', error);
    };

    return () => {
      if (socketRef.current?.readyState === WebSocket.OPEN) {
        socketRef.current.close();
      }
    };
  }, [username]);

  // Função para entrada de nome
  const handleJoinChat = (e: React.FormEvent) => {
    e.preventDefault();
    if (usernameInput.trim()) {
      setUsername(usernameInput.trim());
    }
  };

  // Envio de mensagem
  const sendMessage = (e: React.FormEvent) => {
    e.preventDefault();
    if (!inputMessage.trim() || !socketRef.current) return;

    const message: Partial<Message> = {
      sender: username,
      content: inputMessage.trim(),
      type: 'CHAT',
      timestamp: new Date().toISOString(), // Adiciona o timestamp para a mensagem
      id: new Date().getTime().toString(), // Cria um ID único para a mensagem
    };

    try {
      socketRef.current.send(JSON.stringify(message));
      console.log('📤 Sent message:', message);
      setInputMessage('');
    } catch (error) {
      console.error('❌ Error sending message:', error);
    }
  };

  // Se não estiver logado, exibe o campo para o nome de usuário
  if (!username) {
    return (
      <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">
        <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
          <h1 className="text-2xl font-bold mb-4 text-gray-800">Enter Chat</h1>
          <form onSubmit={handleJoinChat}>
            <input
              type="text"
              value={usernameInput}
              onChange={(e) => setUsernameInput(e.target.value)}
              placeholder="Enter your username"
              className="w-full p-2 border rounded mb-4 focus:outline-none focus:ring-2 focus:ring-blue-400"
              autoComplete="off"
            />
            <button
              type="submit"
              className="w-full bg-blue-500 text-white p-2 rounded hover:bg-blue-600 transition-colors"
              disabled={!usernameInput.trim()}
            >
              Join Chat
            </button>
          </form>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col">
      <header className="bg-white shadow-sm p-4">
        <div className="max-w-4xl mx-auto flex justify-between items-center">
          <h1 className="text-xl font-semibold text-gray-800">Chat Room</h1>
          <div className="flex items-center gap-2">
            <span className={`w-2 h-2 rounded-full ${isConnected ? 'bg-green-500' : 'bg-red-500'}`}></span>
            <span className="text-sm text-gray-600">{username}</span>
          </div>
        </div>
      </header>

      <main className="flex-1 max-w-4xl w-full mx-auto p-4">
        <div className="bg-white rounded-lg shadow-md h-[calc(100vh-12rem)] flex flex-col">
          <div className="flex-1 overflow-y-auto p-4">
            {messages.map((message, index) => (
              <div
                key={message.id || index}
                className={`mb-4 ${message.type === 'CHAT' ? '' : 'text-center'}`}
              >
                {message.type === 'CHAT' ? (
                  <div className={`flex flex-col ${message.sender === username ? 'items-end' : 'items-start'}`}>
                    <div
                      className={`max-w-[70%] rounded-lg p-3 ${message.sender === username ? 'bg-blue-500 text-white' : 'bg-gray-100 text-gray-800'}`}
                    >
                      <p className="text-sm font-semibold mb-1">{message.sender}</p>
                      <p>{message.content}</p>
                    </div>
                    <span className="text-xs text-gray-500 mt-1">
                      {message.timestamp ? format(new Date(message.timestamp), 'HH:mm') : ''}
                    </span>
                  </div>
                ) : (
                  <div className="text-sm text-gray-500 my-2">
                    {message.content}
                  </div>
                )}
              </div>
            ))}
            <div ref={messagesEndRef} />
          </div>

          <form onSubmit={sendMessage} className="p-4 border-t">
            <div className="flex gap-2">
              <input
                type="text"
                value={inputMessage}
                onChange={(e) => setInputMessage(e.target.value)}
                placeholder="Type a message..."
                className="flex-1 p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
                autoComplete="off"
              />
              <button
                type="submit"
                disabled={!inputMessage.trim()}
                className="bg-blue-500 text-white p-2 rounded-lg hover:bg-blue-600 transition-colors disabled:bg-gray-300 disabled:cursor-not-allowed"
              >
                <IoMdSend size={20} />
              </button>
            </div>
          </form>
        </div>
      </main>
    </div>
  );
}

export default App;
