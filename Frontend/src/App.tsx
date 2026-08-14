import { useState } from 'react';
import { pipeline, env } from '@xenova/transformers';
import ImageUpload from './components/ImageUpload';
import Loader from './components/Loader';

// We want to fetch models from the HF hub, no local fallback needed.
env.allowLocalModels = false;

function App() {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [preview, setPreview] = useState<string | null>(null);
  const [isVideo, setIsVideo] = useState(false);
  const [story, setStory] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [loadingStatus, setLoadingStatus] = useState<string>('');
  const [error, setError] = useState<string | null>(null);
  const [frameBlob, setFrameBlob] = useState<Blob | null>(null);

  const extractFrame = (videoUrl: string): Promise<Blob> => {
    return new Promise((resolve, reject) => {
      const video = document.createElement('video');
      video.src = videoUrl;
      video.crossOrigin = 'anonymous';
      video.currentTime = 1; // get frame at 1 second
      video.onloadeddata = () => {
        video.play();
        video.pause();
      };
      video.onseeked = () => {
        const canvas = document.createElement('canvas');
        canvas.width = video.videoWidth;
        canvas.height = video.videoHeight;
        const ctx = canvas.getContext('2d');
        if (ctx) {
          ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
          canvas.toBlob((blob) => {
            if (blob) resolve(blob);
            else reject(new Error('Failed to create blob'));
          }, 'image/jpeg');
        } else {
          reject(new Error('Canvas context failed'));
        }
      };
      video.onerror = (e) => reject(e);
    });
  };

  const handleImageSelect = async (file: File, isVideoFile: boolean) => {
    setSelectedFile(file);
    setIsVideo(isVideoFile);
    setStory(null);
    setError(null);
    setFrameBlob(null);

    const reader = new FileReader();
    reader.onloadend = async () => {
      const url = reader.result as string;
      setPreview(url);
      
      if (isVideoFile) {
        try {
          const blob = await extractFrame(url);
          setFrameBlob(blob);
        } catch (e) {
          console.error("Failed to extract frame", e);
        }
      }
    };
    reader.readAsDataURL(file);
  };

  const handleGenerateStory = async () => {
    const fileToSend = isVideo ? frameBlob : selectedFile;
    if (!fileToSend) {
        setError("No valid image or video frame found.");
        return;
    }

    setLoading(true);
    setLoadingStatus('Initializing AI model (this may take a minute on first load)...');
    setError(null);

    try {
      // Create Object URL for the blob
      const imageUrl = URL.createObjectURL(fileToSend);

      // Load the image-to-text pipeline (runs completely in-browser)
      const captioner = await pipeline('image-to-text', 'Xenova/vit-gpt2-image-captioning', {
        progress_callback: (progress: any) => {
          if (progress.status === 'downloading') {
            setLoadingStatus(`Downloading model weights... ${Math.round(progress.progress || 0)}%`);
          } else if (progress.status === 'init') {
            setLoadingStatus('Initializing model...');
          }
        }
      });

      setLoadingStatus('Analyzing image and generating story...');
      const result = await captioner(imageUrl);
      
      let caption = "A beautiful scene.";
      if (Array.isArray(result) && result.length > 0) {
          caption = (result[0] as any).generated_text || caption;
      }

      // Format it as a simple "story" since small client-side models just give raw captions.
      const generatedStory = `Once upon a time in a place just like this: ${caption}. It was a moment captured perfectly in time.`;
      
      setStory(generatedStory);
      URL.revokeObjectURL(imageUrl);

    } catch (err: any) {
      console.error('Error generating story:', err);
      setError(err.message || 'Failed to run client-side AI model.');
    } finally {
      setLoading(false);
      setLoadingStatus('');
    }
  };

  const handlePlayVoiceover = () => {
    if (!story) return;
    window.speechSynthesis.cancel();
    const utterance = new SpeechSynthesisUtterance(story);
    utterance.rate = 1.0;
    utterance.pitch = 1.0;
    window.speechSynthesis.speak(utterance);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50">
      <div className="container mx-auto px-4 py-12">
        <div className="text-center mb-12">
          <div className="inline-block bg-gradient-to-r from-blue-600 to-purple-600 text-transparent bg-clip-text mb-4">
            <h1 className="text-5xl font-bold">CAPPA AI Storyteller</h1>
          </div>
          <p className="text-gray-600 text-lg max-w-2xl mx-auto">
            Upload a photo or video to generate a creative story with voiceover, powered entirely by your browser's local AI (No server needed!).
          </p>
        </div>

        {error && (
          <div className="max-w-2xl mx-auto mb-8 bg-red-50 border border-red-200 rounded-lg p-4">
            <div className="flex items-start space-x-3">
              <span className="text-red-500 mt-0.5">⚠️</span>
              <div>
                <h3 className="font-semibold text-red-800">Error</h3>
                <p className="text-red-600 text-sm mt-1">{error}</p>
              </div>
            </div>
          </div>
        )}

        <div className="mb-8">
          <ImageUpload onImageSelect={handleImageSelect} preview={preview} isVideo={isVideo} />
        </div>

        {preview && !story && !loading && (
          <div className="text-center">
            <button
              onClick={handleGenerateStory}
              disabled={isVideo && !frameBlob}
              className="bg-gradient-to-r from-blue-600 to-purple-600 text-white px-8 py-4 rounded-lg font-semibold text-lg shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 transition-all duration-200 disabled:opacity-50"
            >
              {isVideo && !frameBlob ? 'Extracting Frame...' : 'Generate Story'}
            </button>
          </div>
        )}

        {loading && (
            <div className="flex flex-col items-center justify-center space-y-4">
                <Loader />
                <p className="text-gray-600 font-medium">{loadingStatus}</p>
            </div>
        )}

        {story && !loading && (
          <div className="max-w-4xl mx-auto mt-12 bg-white rounded-2xl shadow-xl p-8 border border-gray-100">
            <h2 className="text-3xl font-bold text-center mb-6 text-gray-800">
              Your AI Story
            </h2>
            <p className="text-gray-700 text-lg leading-relaxed mb-8 italic">
              "{story}"
            </p>
            <div className="flex justify-center space-x-4">
              <button
                onClick={handlePlayVoiceover}
                className="bg-purple-100 text-purple-700 px-6 py-3 rounded-lg font-semibold hover:bg-purple-200 transition-colors flex items-center"
              >
                🔊 Play Voiceover
              </button>
              <button
                onClick={() => {
                  setStory(null);
                  setPreview(null);
                  setSelectedFile(null);
                  window.speechSynthesis.cancel();
                }}
                className="bg-gray-100 text-gray-700 px-6 py-3 rounded-lg font-semibold hover:bg-gray-200 transition-colors"
              >
                Upload Another
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;
