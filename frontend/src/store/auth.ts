import { create } from 'zustand'
import { clearAuth, loadAuthFromStorage, setBasicAuth } from '../lib/api'

type AuthState = {
  isAuthenticated: boolean
  username: string | null
  login: (username: string, password: string) => Promise<void>
  logout: () => void
}

loadAuthFromStorage()

export const useAuth = create<AuthState>((set) => ({
  isAuthenticated: !!localStorage.getItem('auth.basic'),
  username: localStorage.getItem('auth.user') || null,
  login: async (username: string, password: string) => {
    setBasicAuth(username, password)
    localStorage.setItem('auth.user', username)
    set({ isAuthenticated: true, username })
  },
  logout: () => {
    clearAuth()
    localStorage.removeItem('auth.user')
    set({ isAuthenticated: false, username: null })
  },
}))


