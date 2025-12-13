import { useState } from 'react';
import ImageUpload from './components/ImageUpload';
import CaptionCard from './components/CaptionCard';
import Loader from './components/Loader';

interface Captions {
  short: string;
  funny: string;
  poetic: string;
  travel_tip: string;
}

function App() {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [preview, setPreview] = useState<string | null>(null);
  const [captions, setCaptions] = useState<Captions | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleImageSelect = (file: File) => {
    setSelectedFile(file);
    setCaptions(null);
    setError(null);

    const reader = new FileReader();
    reader.onloadend = () => {
      setPreview(reader.result as string);
    };
    reader.readAsDataURL(file);
  };

  const handleGenerateCaptions = async () => {
    if (!selectedFile) return;

    setLoading(true);
    setError(null);

    try {
      const formData = new FormData();
      formData.append('image', selectedFile);

      const BACKEND =
      import.meta.env.VITE_BACKEND_URL ??
      'https://cappa-backend-sfy0.onrender.com';

      const response = await fetch(`${BACKEND}/api/caption`, {
      method: 'POST',
      body: formData,
      });


      if (!response.ok) {
        throw new Error('Failed to generate captions');
      }

      const data = await response.json();
      setCaptions(data.captions);
    } catch (err) {
      console.error('Error generating captions:', err);
      setError(
        'Unable to connect to the backend server. Please make sure the server is running on http://localhost:4000'
      );
    } finally {
      setLoading(false);
    }
  };

  const captionTypes = [
    { key: 'short' as keyof Captions, title: 'Short Caption', icon: '✨' },
    { key: 'funny' as keyof Captions, title: 'Funny Caption', icon: '😄' },
    { key: 'poetic' as keyof Captions, title: 'Poetic Caption', icon: '🎭' },
    { key: 'travel_tip' as keyof Captions, title: 'Travel Tip', icon: '🧳' },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50">
      <div className="container mx-auto px-4 py-12">
        <div className="text-center mb-12">
          <div className="inline-block bg-gradient-to-r from-blue-600 to-purple-600 text-transparent bg-clip-text mb-4">
            <h1 className="text-5xl font-bold">CAPPA</h1>
          </div>
          <p className="text-gray-600 text-lg max-w-2xl mx-auto">
            Upload your travel photos and get creative captions powered by AI
          </p>
        </div>

        {error && (
          <div className="max-w-2xl mx-auto mb-8 bg-red-50 border border-red-200 rounded-lg p-4">
            <div className="flex items-start space-x-3">
              <svg
                className="w-6 h-6 text-red-500 flex-shrink-0 mt-0.5"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                />
              </svg>
              <div>
                <h3 className="font-semibold text-red-800">Connection Error</h3>
                <p className="text-red-600 text-sm mt-1">{error}</p>
              </div>
            </div>
          </div>
        )}

        <div className="mb-8">
          <ImageUpload onImageSelect={handleImageSelect} preview={preview} />
        </div>

        {preview && !captions && !loading && (
          <div className="text-center">
            <button
              onClick={handleGenerateCaptions}
              className="bg-gradient-to-r from-blue-600 to-purple-600 text-white px-8 py-4 rounded-lg font-semibold text-lg shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 transition-all duration-200"
            >
              Generate Captions
            </button>
          </div>
        )}

        {loading && <Loader />}

        {captions && !loading && (
          <div className="max-w-4xl mx-auto mt-12">
            <h2 className="text-3xl font-bold text-center mb-8 text-gray-800">
              Your Captions
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {captionTypes.map(({ key, title, icon }) => (
                <CaptionCard
                  key={key}
                  title={title}
                  caption={captions[key]}
                  icon={icon}
                />
              ))}
            </div>
            <div className="text-center mt-8">
              <button
                onClick={() => {
                  setCaptions(null);
                  setPreview(null);
                  setSelectedFile(null);
                }}
                className="text-gray-600 hover:text-gray-800 font-medium underline"
              >
                Upload a new image
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;
