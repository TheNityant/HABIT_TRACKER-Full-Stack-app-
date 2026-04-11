// TRANSLATED GOOGLE CODE FOR YOUR PROJECT
const { GoogleGenerativeAI } = require("@google/generative-ai"); // Use the package you actually have

const genAI = new GoogleGenerativeAI("YOUR_API_KEY");

async function main() {
  const model = genAI.getGenerativeModel({ model: "gemini-1.5-flash" }); // Use a stable model name
  const result = await model.generateContent("Explain how AI works in a few words");
  const response = await result.response;
  console.log(response.text());
}

main();