import { Route, Routes } from 'react-router'
import { Layout } from './components/Layout'
import { Detalhe } from './pages/Detalhe'
import { NaoEncontrada } from './pages/NaoEncontrada'
import { Painel } from './pages/Painel'

export function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Painel />} />
        <Route path="/disciplinas/:id" element={<Detalhe />} />
        <Route path="*" element={<NaoEncontrada />} />
      </Routes>
    </Layout>
  )
}
